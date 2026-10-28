package tz.co.halotel.halokazi.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tools.jackson.databind.json.JsonMapper;

/**
 * Real Oracle, real services, test-thread MockMvc. Every test method runs in one transaction that
 * is rolled back, and all service transactions join it, so nothing is left in the database.
 * <p>
 * All requests of a test share one persistence context: use {@link #exec} (flush + SQL + clear)
 * to change rows behind Hibernate's back, and the query helpers (which flush first) to inspect them.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestSmsConfig.class)
public abstract class AbstractIntegrationTest {

    public static final String PASSWORD = "Str0ng!Pass";
    private static final JsonMapper JSON = JsonMapper.builder().build();
    /** +255 69x xxx xxx: never used by real/E2E data; rows are rolled back anyway. */
    private static final AtomicInteger PHONE_SEQ =
            new AtomicInteger(ThreadLocalRandom.current().nextInt(0, 9_000_000));

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected JdbcTemplate jdbc;
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    protected CapturingSmsSender sms;

    @BeforeEach
    void resetSms() {
        sms.reset();
    }

    // ---------------------------------------------------------------- data

    protected static String newPhone() {
        return "+25569" + String.format("%07d", PHONE_SEQ.incrementAndGet());
    }

    protected static String newEmail() {
        return "it-" + UUID.randomUUID().toString().substring(0, 12) + "@halokazi.test";
    }

    protected static Map<String, Object> registerBody(String role, String phone, String email) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("role", role);
        body.put("fullName", "Test Person");
        body.put("phone", phone);
        body.put("email", email);
        body.put("password", PASSWORD);
        body.put("repeatPassword", PASSWORD);
        return body;
    }

    public record Registered(long userId, String phone, String email, String code) {
    }

    public record Active(long userId, String phone, String email, String token) {
    }

    protected Registered register(String role) throws Exception {
        String phone = newPhone();
        String email = newEmail();
        MvcResult result = postJson("/api/auth/register", registerBody(role, phone, email))
                .andExpect(status().isCreated()).andReturn();
        long userId = ((Number) JsonPath.read(body(result), "$.userId")).longValue();
        return new Registered(userId, phone, email, sms.lastCodeFor(phone));
    }

    protected Active registerAndVerify(String role) throws Exception {
        Registered r = register(role);
        String token = verify(r.phone(), r.code());
        return new Active(r.userId(), r.phone(), r.email(), token);
    }

    protected String verify(String phone, String code) throws Exception {
        MvcResult result = postJson("/api/auth/otp/verify", Map.of("phone", phone, "code", code))
                .andExpect(status().isOk()).andReturn();
        return JsonPath.read(body(result), "$.token");
    }

    protected String login(String identifier, String password) throws Exception {
        MvcResult result = postJson("/api/auth/login", Map.of("identifier", identifier, "password", password))
                .andExpect(status().isOk()).andReturn();
        return JsonPath.read(body(result), "$.token");
    }

    // ---------------------------------------------------------------- http

    protected ResultActions postJson(String url, Object body) throws Exception {
        return mvc.perform(json(post(url), body));
    }

    protected ResultActions me(String bearer) throws Exception {
        return mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer));
    }

    protected ResultActions logout(String bearer) throws Exception {
        return mvc.perform(post("/api/auth/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer));
    }

    private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder, Object body) {
        return builder.contentType(MediaType.APPLICATION_JSON)
                .content(body instanceof String s ? s : JSON.writeValueAsString(body));
    }

    protected static String body(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString();
    }

    // ---------------------------------------------------------------- db

    protected int queryInt(String sql, Object... args) {
        em.flush();
        Integer value = jdbc.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    protected String queryString(String sql, Object... args) {
        em.flush();
        return jdbc.queryForObject(sql, String.class, args);
    }

    protected List<String> queryStrings(String sql, Object... args) {
        em.flush();
        return jdbc.queryForList(sql, String.class, args);
    }

    /** Changes rows behind Hibernate's back and drops the (now stale) persistence context. */
    protected int exec(String sql, Object... args) {
        em.flush();
        int updated = jdbc.update(sql, args);
        em.clear();
        return updated;
    }

    protected List<String> auditActions(long userId) {
        return queryStrings("select action from audit_log where user_id = ? order by audit_id", userId);
    }
}
