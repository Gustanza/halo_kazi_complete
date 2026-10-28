--------------------------------------------------------------------------------
-- HALOKAZI - Oracle 19c business-rule enforcement
-- Functions, triggers, lifecycle procedures and reporting views.
-- Run after 01_schema.sql. Blocks end with "/" for SQL*Plus / SQLcl / SQL Developer.
--
-- Error codes raised (app can map them to friendly EN/SW messages):
--   -20001 profile completion below threshold      -20010 vacancy not open for applications
--   -20002 CV not owned / deleted                  -20011 deadline must be in the future
--   -20003 job seeker account not active           -20012 invalid vacancy status transition
--   -20004 invalid application status transition   -20013 critical vacancy field locked
--   -20005 application locked (not editable)       -20014 employer not approved / not active
--   -20006 application fields immutable            -20015 reopen requires approved request
--   -20007 applications cannot be withdrawn        -20020 verified employer field locked
--   -20008 certificate not owned / deleted         -20021 verification decision is final
--   -20030 record is append-only                   -20040 permission assignment not allowed
--------------------------------------------------------------------------------

--==============================================================================
-- Helpers
--==============================================================================

CREATE OR REPLACE FUNCTION hk_setting_num (p_key IN VARCHAR2, p_default IN NUMBER)
RETURN NUMBER IS
    v_value system_setting.setting_value%TYPE;
BEGIN
    SELECT setting_value INTO v_value FROM system_setting WHERE setting_key = p_key;
    RETURN TO_NUMBER(v_value);
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN p_default;
END hk_setting_num;
/

-- Weighted profile completion (Requirements section 5). Sections marked Not Applicable
-- count as complete only when profile_section.can_be_na = 1.
CREATE OR REPLACE FUNCTION hk_profile_completion (p_profile_id IN NUMBER)
RETURN NUMBER IS
    v_score NUMBER;
BEGIN
    SELECT NVL(SUM(ps.weight), 0)
      INTO v_score
      FROM profile_section ps
     WHERE ps.is_active = 1
       AND (   (ps.can_be_na = 1 AND EXISTS (SELECT 1 FROM profile_section_na na
                                              WHERE na.profile_id = p_profile_id
                                                AND na.section_code = ps.section_code))
            OR (ps.section_code = 'CONTACT' AND EXISTS (
                    SELECT 1 FROM job_seeker_profile p JOIN app_user u ON u.user_id = p.user_id
                     WHERE p.profile_id = p_profile_id AND u.email IS NOT NULL AND u.phone_verified = 1))
            OR (ps.section_code = 'EDUCATION' AND EXISTS (
                    SELECT 1 FROM education e WHERE e.profile_id = p_profile_id))
            OR (ps.section_code = 'WORK_EXPERIENCE' AND EXISTS (
                    SELECT 1 FROM work_experience w WHERE w.profile_id = p_profile_id))
            OR (ps.section_code = 'SKILLS' AND EXISTS (
                    SELECT 1 FROM profile_skill s WHERE s.profile_id = p_profile_id))
            OR (ps.section_code = 'CERTIFICATIONS' AND EXISTS (
                    SELECT 1 FROM certification c WHERE c.profile_id = p_profile_id AND c.deleted_at IS NULL))
            OR (ps.section_code = 'LANGUAGES' AND EXISTS (
                    SELECT 1 FROM profile_language l WHERE l.profile_id = p_profile_id))
            OR (ps.section_code = 'ADDRESS' AND EXISTS (
                    SELECT 1 FROM job_seeker_profile p WHERE p.profile_id = p_profile_id AND p.region_id IS NOT NULL))
            OR (ps.section_code = 'SUMMARY' AND EXISTS (
                    SELECT 1 FROM job_seeker_profile p WHERE p.profile_id = p_profile_id
                       AND p.professional_summary IS NOT NULL AND DBMS_LOB.GETLENGTH(p.professional_summary) > 0))
            OR (ps.section_code = 'PROFILE_PICTURE' AND EXISTS (
                    SELECT 1 FROM job_seeker_profile p WHERE p.profile_id = p_profile_id
                       AND p.profile_picture_file_id IS NOT NULL))
           );
    RETURN LEAST(v_score, 100);
END hk_profile_completion;
/

-- App calls this after any profile/section change to refresh the cached percentage (PROF-02).
CREATE OR REPLACE PROCEDURE hk_refresh_profile_completion (p_profile_id IN NUMBER) IS
BEGIN
    UPDATE job_seeker_profile
       SET completion_percentage = hk_profile_completion(p_profile_id),
           completion_updated_at = SYSTIMESTAMP
     WHERE profile_id = p_profile_id;
END hk_refresh_profile_completion;
/

--==============================================================================
-- Append-only tables (audit log, status histories)
--==============================================================================

CREATE OR REPLACE TRIGGER trg_audit_log_immutable
BEFORE UPDATE OR DELETE ON audit_log
BEGIN
    RAISE_APPLICATION_ERROR(-20030, 'AUDIT_LOG is append-only');
END;
/

CREATE OR REPLACE TRIGGER trg_ash_immutable
BEFORE UPDATE OR DELETE ON application_status_history
BEGIN
    RAISE_APPLICATION_ERROR(-20030, 'APPLICATION_STATUS_HISTORY is append-only');
END;
/

CREATE OR REPLACE TRIGGER trg_vsh_immutable
BEFORE UPDATE OR DELETE ON vacancy_status_history
BEGIN
    RAISE_APPLICATION_ERROR(-20030, 'VACANCY_STATUS_HISTORY is append-only');
END;
/

--==============================================================================
-- Admin permissions: only Super Admin grants/revokes, only to Admins, never to self
--==============================================================================

CREATE OR REPLACE TRIGGER trg_apa_validate
BEFORE INSERT OR UPDATE ON admin_permission_assignment
FOR EACH ROW
DECLARE
    v_role app_user.role%TYPE;
BEGIN
    IF UPDATING AND (:NEW.user_id <> :OLD.user_id OR :NEW.permission_id <> :OLD.permission_id
                     OR :NEW.assigned_by <> :OLD.assigned_by) THEN
        RAISE_APPLICATION_ERROR(-20040, 'Only revocation may be recorded on an assignment');
    END IF;

    SELECT role INTO v_role FROM app_user WHERE user_id = :NEW.user_id;
    IF v_role <> 'ADMIN' THEN
        RAISE_APPLICATION_ERROR(-20040, 'Permissions can only be assigned to ADMIN users');
    END IF;

    SELECT role INTO v_role FROM app_user WHERE user_id = NVL(:NEW.revoked_by, :NEW.assigned_by);
    IF v_role <> 'SUPER_ADMIN' THEN
        RAISE_APPLICATION_ERROR(-20040, 'Only a SUPER_ADMIN can assign or revoke permissions');
    END IF;
END;
/

--==============================================================================
-- Employer: verified identity is locked after approval (VER-10)
--==============================================================================

CREATE OR REPLACE TRIGGER trg_employer_lock_verified
BEFORE UPDATE ON employer
FOR EACH ROW
BEGIN
    IF :OLD.verification_status = 'APPROVED' AND :NEW.anonymized_at IS NULL AND (
           NVL(:NEW.business_name, '~')           <> NVL(:OLD.business_name, '~')
        OR NVL(:NEW.registration_number, '~')     <> NVL(:OLD.registration_number, '~')
        OR NVL(:NEW.tin, '~')                     <> NVL(:OLD.tin, '~')
        OR NVL(:NEW.business_licence_number, '~') <> NVL(:OLD.business_licence_number, '~')
        OR :NEW.verification_status               <> :OLD.verification_status) THEN
        RAISE_APPLICATION_ERROR(-20020, 'Verified company information cannot be changed after approval');
    END IF;
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

-- Verification decisions are final; history is retained (VER-06/07)
CREATE OR REPLACE TRIGGER trg_ev_decision_final
BEFORE UPDATE OR DELETE ON employer_verification
FOR EACH ROW
BEGIN
    IF DELETING THEN
        RAISE_APPLICATION_ERROR(-20021, 'Verification history cannot be deleted');
    END IF;
    IF :OLD.status <> 'PENDING_REVIEW' THEN
        RAISE_APPLICATION_ERROR(-20021, 'Verification decision is final; submit a new verification');
    END IF;
    IF :NEW.status <> 'PENDING_REVIEW' AND :NEW.reviewed_at IS NULL THEN
        :NEW.reviewed_at := SYSTIMESTAMP;
    END IF;
END;
/

--==============================================================================
-- Vacancy lifecycle (section 8, AD-05)
--   DRAFT -> ACTIVE | EXPIRED | DELETED
--   ACTIVE -> CLOSED | DELETED
--   CLOSED -> ACTIVE (only with an APPROVED reopen request) | DELETED
--==============================================================================

CREATE OR REPLACE TRIGGER trg_vacancy_validate
BEFORE INSERT OR UPDATE ON vacancy
FOR EACH ROW
DECLARE
    v_emp_status  employer.verification_status%TYPE;
    v_user_status app_user.account_status%TYPE;
    v_apps        NUMBER;
    v_ok          NUMBER;
BEGIN
    IF INSERTING THEN
        :NEW.status := NVL(:NEW.status, 'DRAFT');
        IF :NEW.status NOT IN ('DRAFT', 'ACTIVE') THEN
            RAISE_APPLICATION_ERROR(-20012, 'A new vacancy must be DRAFT or ACTIVE');
        END IF;
        IF :NEW.status = 'DRAFT' THEN
            :NEW.draft_expires_at := SYSTIMESTAMP
                + NUMTODSINTERVAL(hk_setting_num('DRAFT_VACANCY_EXPIRY_DAYS', 14), 'DAY');
        END IF;
    END IF;

    -- VAC-01 / AUTH-08 / VAC-11: approved, active employer only
    IF INSERTING OR (:NEW.status = 'ACTIVE' AND :OLD.status <> 'ACTIVE') THEN
        SELECT e.verification_status, u.account_status
          INTO v_emp_status, v_user_status
          FROM employer e JOIN app_user u ON u.user_id = e.user_id
         WHERE e.employer_id = :NEW.employer_id;
        IF v_emp_status <> 'APPROVED' OR v_user_status <> 'ACTIVE' THEN
            RAISE_APPLICATION_ERROR(-20014, 'Employer is not approved or not active');
        END IF;
    END IF;

    IF UPDATING AND :NEW.status <> :OLD.status THEN
        IF NOT (   (:OLD.status = 'DRAFT'  AND :NEW.status IN ('ACTIVE', 'EXPIRED', 'DELETED'))
                OR (:OLD.status = 'ACTIVE' AND :NEW.status IN ('CLOSED', 'DELETED'))
                OR (:OLD.status = 'CLOSED' AND :NEW.status IN ('ACTIVE', 'DELETED'))) THEN
            RAISE_APPLICATION_ERROR(-20012,
                'Invalid vacancy status change ' || :OLD.status || ' -> ' || :NEW.status);
        END IF;

        -- VAC-10: reopening requires an approved request decided after the closure
        IF :OLD.status = 'CLOSED' AND :NEW.status = 'ACTIVE' THEN
            SELECT COUNT(*) INTO v_ok
              FROM vacancy_reopen_request r
             WHERE r.vacancy_id = :NEW.vacancy_id
               AND r.status = 'APPROVED'
               AND r.reviewed_at >= :OLD.closed_at;
            IF v_ok = 0 THEN
                RAISE_APPLICATION_ERROR(-20015, 'Reopening requires an Admin-approved reopen request');
            END IF;
            :NEW.closed_at      := NULL;
            :NEW.closure_reason := NULL;
        END IF;

        IF :NEW.status = 'ACTIVE' AND :NEW.published_at IS NULL THEN
            :NEW.published_at := SYSTIMESTAMP;
        END IF;
        IF :NEW.status = 'CLOSED' AND :NEW.closed_at IS NULL THEN
            :NEW.closed_at := SYSTIMESTAMP;
        END IF;
        IF :NEW.status = 'DELETED' AND :NEW.deleted_at IS NULL THEN
            :NEW.deleted_at := SYSTIMESTAMP;
        END IF;
    END IF;

    IF INSERTING AND :NEW.status = 'ACTIVE' THEN
        :NEW.published_at := SYSTIMESTAMP;
    END IF;

    -- VAC-09: deadline cannot be in the past whenever a vacancy is (re)published or its deadline changes
    IF :NEW.status IN ('DRAFT', 'ACTIVE')
       AND (INSERTING OR :NEW.status <> :OLD.status OR :NEW.application_deadline <> :OLD.application_deadline)
       AND :NEW.application_deadline <= SYSTIMESTAMP THEN
        RAISE_APPLICATION_ERROR(-20011, 'Application deadline must be in the future');
    END IF;

    -- VAC-04: once applications exist, critical fields are frozen
    IF UPDATING THEN
        SELECT COUNT(*) INTO v_apps FROM application WHERE vacancy_id = :NEW.vacancy_id;
        IF v_apps > 0 AND (
               :NEW.job_title <> :OLD.job_title
            OR DBMS_LOB.COMPARE(:NEW.job_requirements, :OLD.job_requirements) <> 0
            OR DBMS_LOB.COMPARE(:NEW.qualifications, :OLD.qualifications) <> 0
            OR NVL(:NEW.min_education_level_id, -1) <> NVL(:OLD.min_education_level_id, -1)
            OR :NEW.min_experience_years <> :OLD.min_experience_years
            OR :NEW.employment_type_id <> :OLD.employment_type_id
            OR :NEW.region_id <> :OLD.region_id
            OR :NEW.employer_id <> :OLD.employer_id) THEN
            RAISE_APPLICATION_ERROR(-20013, 'Critical vacancy fields cannot change once applications exist');
        END IF;
        :NEW.updated_at := SYSTIMESTAMP;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_vacancy_history
AFTER INSERT OR UPDATE OF status ON vacancy
FOR EACH ROW
BEGIN
    IF INSERTING OR :NEW.status <> :OLD.status THEN
        INSERT INTO vacancy_status_history (vacancy_id, from_status, to_status, reason, changed_by)
        VALUES (:NEW.vacancy_id,
                :OLD.status,                       -- NULL on insert
                :NEW.status,
                :NEW.closure_reason,
                :NEW.status_changed_by);
    END IF;
END;
/

--==============================================================================
-- Applications (section 10, AD-03, AD-04)
--==============================================================================

CREATE OR REPLACE TRIGGER trg_application_before_insert
BEFORE INSERT ON application
FOR EACH ROW
DECLARE
    v_vac_status   vacancy.status%TYPE;
    v_deadline     vacancy.application_deadline%TYPE;
    v_emp_user_st  app_user.account_status%TYPE;
    v_seeker_st    app_user.account_status%TYPE;
    v_seeker_id    app_user.user_id%TYPE;
    v_cv_owner     cv.profile_id%TYPE;
    v_cv_deleted   cv.deleted_at%TYPE;
    v_score        NUMBER;
BEGIN
    -- APP-02 / VAC-08 / VAC-11: vacancy active, before deadline, employer not suspended
    SELECT v.status, v.application_deadline, u.account_status
      INTO v_vac_status, v_deadline, v_emp_user_st
      FROM vacancy v
      JOIN employer e ON e.employer_id = v.employer_id
      JOIN app_user u ON u.user_id = e.user_id
     WHERE v.vacancy_id = :NEW.vacancy_id;
    IF v_vac_status <> 'ACTIVE' OR v_deadline <= SYSTIMESTAMP OR v_emp_user_st <> 'ACTIVE' THEN
        RAISE_APPLICATION_ERROR(-20010, 'This vacancy is not accepting applications');
    END IF;

    SELECT u.account_status, u.user_id INTO v_seeker_st, v_seeker_id
      FROM job_seeker_profile p JOIN app_user u ON u.user_id = p.user_id
     WHERE p.profile_id = :NEW.profile_id;
    IF v_seeker_st <> 'ACTIVE' THEN
        RAISE_APPLICATION_ERROR(-20003, 'Job seeker account is not active');
    END IF;

    -- APP-03 / CV-07: exactly one CV, owned by the applicant
    SELECT profile_id, deleted_at INTO v_cv_owner, v_cv_deleted FROM cv WHERE cv_id = :NEW.cv_id;
    IF v_cv_owner <> :NEW.profile_id OR v_cv_deleted IS NOT NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Selected CV does not belong to this job seeker');
    END IF;

    -- APP-01: profile completion threshold (computed live, not from the cached column)
    v_score := hk_profile_completion(:NEW.profile_id);
    IF v_score < hk_setting_num('PROFILE_MIN_COMPLETION', 75) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Profile completion ' || v_score || '% is below the required minimum');
    END IF;

    :NEW.profile_completion_at_submit := v_score;
    :NEW.status            := 'SUBMITTED';
    :NEW.submitted_at      := SYSTIMESTAMP;
    :NEW.status_changed_at := SYSTIMESTAMP;
    :NEW.status_changed_by := v_seeker_id;           -- history shows the applicant submitted it
    :NEW.created_at        := SYSTIMESTAMP;
    :NEW.updated_at        := SYSTIMESTAMP;
    IF :NEW.application_ref IS NULL THEN
        :NEW.application_ref := 'HK-' || TO_CHAR(SYSDATE, 'YYYY') || '-'
                                || LPAD(seq_application_ref.NEXTVAL, 6, '0');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_application_before_update
BEFORE UPDATE ON application
FOR EACH ROW
DECLARE
    v_deadline vacancy.application_deadline%TYPE;
BEGIN
    -- CV-08 / APP-13 / PROF-04: what was submitted never changes
    IF :NEW.vacancy_id <> :OLD.vacancy_id
       OR :NEW.profile_id <> :OLD.profile_id
       OR :NEW.cv_id <> :OLD.cv_id
       OR :NEW.cv_snapshot_file_id <> :OLD.cv_snapshot_file_id
       OR :NEW.application_ref <> :OLD.application_ref
       OR :NEW.submitted_at <> :OLD.submitted_at
       OR :NEW.profile_completion_at_submit <> :OLD.profile_completion_at_submit
       OR DBMS_LOB.COMPARE(:NEW.cv_snapshot, :OLD.cv_snapshot) <> 0
       OR DBMS_LOB.COMPARE(:NEW.profile_snapshot, :OLD.profile_snapshot) <> 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Submitted application data and CV cannot be changed');
    END IF;

    -- APP-07/08/09: seeker edits only while SUBMITTED and before the deadline
    IF DBMS_LOB.COMPARE(:NEW.cover_letter, :OLD.cover_letter) <> 0 THEN
        SELECT application_deadline INTO v_deadline FROM vacancy WHERE vacancy_id = :NEW.vacancy_id;
        IF :OLD.status <> 'SUBMITTED' OR v_deadline <= SYSTIMESTAMP THEN
            RAISE_APPLICATION_ERROR(-20005, 'Application can no longer be edited');
        END IF;
        :NEW.last_edited_at := SYSTIMESTAMP;
    END IF;

    -- APP-11: Submitted -> In Review -> Rejected | Shortlisted
    IF :NEW.status <> :OLD.status THEN
        IF NOT (   (:OLD.status = 'SUBMITTED' AND :NEW.status = 'IN_REVIEW')
                OR (:OLD.status = 'IN_REVIEW' AND :NEW.status IN ('REJECTED', 'SHORTLISTED'))) THEN
            RAISE_APPLICATION_ERROR(-20004,
                'Invalid application status change ' || :OLD.status || ' -> ' || :NEW.status);
        END IF;
        :NEW.status_changed_at := SYSTIMESTAMP;
    END IF;

    :NEW.updated_at := SYSTIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_application_no_delete
BEFORE DELETE ON application
BEGIN
    RAISE_APPLICATION_ERROR(-20007, 'Submitted applications cannot be withdrawn or deleted');
END;
/

-- APP-12: every status (including the initial SUBMITTED) is written to the immutable history
CREATE OR REPLACE TRIGGER trg_application_history
AFTER INSERT OR UPDATE OF status ON application
FOR EACH ROW
BEGIN
    IF INSERTING OR :NEW.status <> :OLD.status THEN
        INSERT INTO application_status_history
            (application_id, from_status, to_status, changed_by, changed_at, remarks)
        VALUES
            (:NEW.application_id,
             :OLD.status,                          -- NULL on insert
             :NEW.status,
             :NEW.status_changed_by,
             :NEW.status_changed_at,
             :NEW.status_remarks);
    END IF;
END;
/

-- Certificates: only the applicant's own, only while the application is editable; details are frozen
CREATE OR REPLACE TRIGGER trg_app_cert_validate
BEFORE INSERT OR UPDATE OR DELETE ON application_certificate
FOR EACH ROW
DECLARE
    v_app_id     application.application_id%TYPE := NVL(:NEW.application_id, :OLD.application_id);
    v_status     application.status%TYPE;
    v_profile    application.profile_id%TYPE;
    v_deadline   vacancy.application_deadline%TYPE;
    v_cert_owner certification.profile_id%TYPE;
    v_cert_del   certification.deleted_at%TYPE;
BEGIN
    IF UPDATING THEN
        RAISE_APPLICATION_ERROR(-20006, 'Attached certificate details cannot be changed');
    END IF;

    SELECT a.status, a.profile_id, v.application_deadline
      INTO v_status, v_profile, v_deadline
      FROM application a JOIN vacancy v ON v.vacancy_id = a.vacancy_id
     WHERE a.application_id = v_app_id;

    IF v_status <> 'SUBMITTED' OR v_deadline <= SYSTIMESTAMP THEN
        RAISE_APPLICATION_ERROR(-20005, 'Application can no longer be edited');
    END IF;

    IF INSERTING THEN
        SELECT profile_id, deleted_at, certification_name, issuing_organization,
               issue_date, expiry_date, certificate_file_id
          INTO v_cert_owner, v_cert_del, :NEW.certification_name, :NEW.issuing_organization,
               :NEW.issue_date, :NEW.expiry_date, :NEW.certificate_file_id
          FROM certification
         WHERE certification_id = :NEW.certification_id;
        IF v_cert_owner <> v_profile OR v_cert_del IS NOT NULL THEN
            RAISE_APPLICATION_ERROR(-20008, 'Certificate does not belong to this job seeker');
        END IF;
        :NEW.attached_at := SYSTIMESTAMP;
    END IF;
END;
/

--==============================================================================
-- Scheduled lifecycle jobs (VAC-02, VAC-06, session/OTP housekeeping)
--==============================================================================

CREATE OR REPLACE PROCEDURE hk_run_lifecycle_jobs IS
BEGIN
    -- Auto-close vacancies whose deadline has passed (VAC-06)
    UPDATE vacancy
       SET status = 'CLOSED', closure_reason = 'DEADLINE_REACHED',
           closed_at = SYSTIMESTAMP, status_changed_by = NULL
     WHERE status = 'ACTIVE' AND application_deadline <= SYSTIMESTAMP;

    -- Expire drafts left unpublished for 14 days (VAC-02)
    UPDATE vacancy
       SET status = 'EXPIRED', status_changed_by = NULL
     WHERE status = 'DRAFT' AND draft_expires_at <= SYSTIMESTAMP;

    -- Revoke idle sessions (AUTH-12)
    UPDATE user_session
       SET revoked_at = SYSTIMESTAMP, revoke_reason = 'IDLE_TIMEOUT'
     WHERE revoked_at IS NULL
       AND last_activity_at <= SYSTIMESTAMP
           - NUMTODSINTERVAL(hk_setting_num('SESSION_IDLE_TIMEOUT_MINUTES', 10), 'MINUTE');

    COMMIT;
END hk_run_lifecycle_jobs;
/

-- Runs every 5 minutes. Applications are blocked at the deadline by trigger even between runs.
BEGIN
    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'HK_LIFECYCLE_JOB',
        job_type        => 'STORED_PROCEDURE',
        job_action      => 'HK_RUN_LIFECYCLE_JOBS',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=MINUTELY;INTERVAL=5',
        enabled         => TRUE,
        comments        => 'Halokazi: auto-close vacancies, expire drafts, revoke idle sessions');
END;
/

--==============================================================================
-- Views
--==============================================================================

-- Vacancies open for search and applications (VAC-08, VAC-11)
CREATE OR REPLACE VIEW v_open_vacancy AS
SELECT v.*
  FROM vacancy v
  JOIN employer e ON e.employer_id = v.employer_id
  JOIN app_user u ON u.user_id = e.user_id
 WHERE v.status = 'ACTIVE'
   AND v.application_deadline > SYSTIMESTAMP
   AND e.verification_status = 'APPROVED'
   AND u.account_status = 'ACTIVE';

-- Active admin permissions (revoked or non-active admins excluded)
CREATE OR REPLACE VIEW v_admin_effective_permission AS
SELECT a.user_id, p.permission_id, p.code AS permission_code, a.assigned_by, a.assigned_at
  FROM admin_permission_assignment a
  JOIN admin_permission p ON p.permission_id = a.permission_id
  JOIN app_user u ON u.user_id = a.user_id
 WHERE a.revoked_at IS NULL
   AND u.role = 'ADMIN'
   AND u.account_status = 'ACTIVE';

-- Employer analytics: per-vacancy application counts (section 12.3)
CREATE OR REPLACE VIEW v_employer_vacancy_stats AS
SELECT v.employer_id,
       v.vacancy_id,
       v.job_title,
       v.status AS vacancy_status,
       v.published_at,
       v.application_deadline,
       COUNT(a.application_id)                                    AS total_applications,
       SUM(CASE WHEN a.status = 'SUBMITTED'   THEN 1 ELSE 0 END)  AS submitted,
       SUM(CASE WHEN a.status = 'IN_REVIEW'   THEN 1 ELSE 0 END)  AS in_review,
       SUM(CASE WHEN a.status = 'SHORTLISTED' THEN 1 ELSE 0 END)  AS shortlisted,
       SUM(CASE WHEN a.status = 'REJECTED'    THEN 1 ELSE 0 END)  AS rejected,
       (SELECT COUNT(*) FROM saved_vacancy s WHERE s.vacancy_id = v.vacancy_id) AS times_saved
  FROM vacancy v
  LEFT JOIN application a ON a.vacancy_id = v.vacancy_id
 WHERE v.status <> 'DELETED'
 GROUP BY v.employer_id, v.vacancy_id, v.job_title, v.status, v.published_at, v.application_deadline;

-- Job seeker's own application statistics (section 12.3)
CREATE OR REPLACE VIEW v_job_seeker_application_stats AS
SELECT p.profile_id,
       p.user_id,
       COUNT(a.application_id)                                    AS total_applications,
       SUM(CASE WHEN a.status = 'SUBMITTED'   THEN 1 ELSE 0 END)  AS submitted,
       SUM(CASE WHEN a.status = 'IN_REVIEW'   THEN 1 ELSE 0 END)  AS in_review,
       SUM(CASE WHEN a.status = 'SHORTLISTED' THEN 1 ELSE 0 END)  AS shortlisted,
       SUM(CASE WHEN a.status = 'REJECTED'    THEN 1 ELSE 0 END)  AS rejected
  FROM job_seeker_profile p
  LEFT JOIN application a ON a.profile_id = p.profile_id
 GROUP BY p.profile_id, p.user_id;

-- Rule-based relevance (section 9): filter by vacancy_id or profile_id when querying.
-- score = category match (3) + region match (2) + employment type match (1)
--       + matched skills (1 each) ; app decides the cut-off for "relevant".
CREATE OR REPLACE VIEW v_vacancy_profile_match AS
SELECT v.vacancy_id,
       p.profile_id,
       CASE WHEN EXISTS (SELECT 1 FROM profile_preferred_category c
                          WHERE c.profile_id = p.profile_id AND c.category_id = v.category_id)
            THEN 1 ELSE 0 END AS category_match,
       CASE WHEN EXISTS (SELECT 1 FROM profile_preferred_region r
                          WHERE r.profile_id = p.profile_id AND r.region_id = v.region_id)
                 OR p.region_id = v.region_id
            THEN 1 ELSE 0 END AS region_match,
       CASE WHEN EXISTS (SELECT 1 FROM profile_preferred_emp_type t
                          WHERE t.profile_id = p.profile_id AND t.employment_type_id = v.employment_type_id)
            THEN 1 ELSE 0 END AS employment_type_match,
       (SELECT COUNT(*) FROM vacancy_skill vs
          JOIN profile_skill ps ON ps.skill_id = vs.skill_id
         WHERE vs.vacancy_id = v.vacancy_id AND ps.profile_id = p.profile_id) AS matched_skills,
       (SELECT COUNT(*) FROM vacancy_skill vs WHERE vs.vacancy_id = v.vacancy_id) AS required_skills
  FROM v_open_vacancy v
  CROSS JOIN job_seeker_profile p
  JOIN app_user u ON u.user_id = p.user_id
 WHERE u.account_status = 'ACTIVE'
   AND p.receive_job_alerts = 1;
