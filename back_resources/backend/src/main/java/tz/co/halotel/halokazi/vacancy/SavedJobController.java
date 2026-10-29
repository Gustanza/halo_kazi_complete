package tz.co.halotel.halokazi.vacancy;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;
import tz.co.halotel.halokazi.session.AuthenticatedUser;
import tz.co.halotel.halokazi.session.CurrentUser;

/** A job seeker's bookmarked jobs (saved_vacancy). Saving and un-saving are idempotent. */
@RestController
@RequestMapping("/api/me/saved-jobs")
@Transactional
class SavedJobController {

    record SavedJobIds(List<Long> vacancyIds) {
    }

    private final JdbcClient jdbc;

    SavedJobController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Ids only, so a job list can show which cards are bookmarked. */
    @GetMapping("/ids")
    @Transactional(readOnly = true)
    SavedJobIds ids(@CurrentUser AuthenticatedUser user) {
        return new SavedJobIds(jdbc.sql("SELECT vacancy_id FROM saved_vacancy WHERE profile_id = :profileId"
                        + " ORDER BY saved_at DESC")
                .param("profileId", profileId(user))
                .query(Long.class)
                .list());
    }

    /** Only open jobs can be saved; saving twice is fine. */
    @PutMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void save(@CurrentUser AuthenticatedUser user, @PathVariable long vacancyId) {
        long profileId = profileId(user);
        boolean open = jdbc.sql("SELECT COUNT(*) FROM v_open_vacancy WHERE vacancy_id = :id")
                .param("id", vacancyId).query(Long.class).single() > 0;
        if (!open) {
            throw new ApiException(ErrorCode.JOB_NOT_FOUND);
        }
        jdbc.sql("""
                MERGE INTO saved_vacancy s
                USING (SELECT :profileId profile_id, :vacancyId vacancy_id FROM dual) n
                   ON (s.profile_id = n.profile_id AND s.vacancy_id = n.vacancy_id)
                 WHEN NOT MATCHED THEN INSERT (profile_id, vacancy_id) VALUES (n.profile_id, n.vacancy_id)
                """)
                .param("profileId", profileId)
                .param("vacancyId", vacancyId)
                .update();
    }

    @DeleteMapping("/{vacancyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unsave(@CurrentUser AuthenticatedUser user, @PathVariable long vacancyId) {
        jdbc.sql("DELETE FROM saved_vacancy WHERE profile_id = :profileId AND vacancy_id = :vacancyId")
                .param("profileId", profileId(user))
                .param("vacancyId", vacancyId)
                .update();
    }

    private long profileId(AuthenticatedUser user) {
        return jdbc.sql("SELECT profile_id FROM job_seeker_profile WHERE user_id = :userId")
                .param("userId", user.userId())
                .query(Long.class)
                .optional()
                .orElseThrow(() -> new ApiException(ErrorCode.JOB_SEEKERS_ONLY));
    }
}
