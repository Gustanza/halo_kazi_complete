package tz.co.halotel.halokazi.common.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import tz.co.halotel.halokazi.support.AbstractIntegrationTest;

class CorsTest extends AbstractIntegrationTest {

    private static final String UI = "http://localhost:5173";

    @Test
    void preflightFromUiIsAllowed() throws Exception {
        mvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, UI)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")));
    }

    @Test
    void preflightForProtectedRouteSkipsAuthentication() throws Exception {
        mvc.perform(options("/api/auth/me")
                        .header(HttpHeaders.ORIGIN, UI)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI));
    }

    @Test
    void preflightFromOtherOriginIsRejected() throws Exception {
        mvc.perform(options("/api/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void actualRequestFromUiExposesRetryAfter() throws Exception {
        mvc.perform(post("/api/auth/otp/resend").header(HttpHeaders.ORIGIN, UI)
                        .contentType("application/json").content("{\"phone\":\"" + newPhone() + "\"}"))
                .andExpect(status().isAccepted())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, UI))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, containsString("Retry-After")));
    }

    @Test
    void actualRequestFromOtherOriginIsRejected() throws Exception {
        mvc.perform(post("/api/auth/otp/resend").header(HttpHeaders.ORIGIN, "http://evil.example")
                        .contentType("application/json").content("{\"phone\":\"" + newPhone() + "\"}"))
                .andExpect(status().isForbidden());
    }
}
