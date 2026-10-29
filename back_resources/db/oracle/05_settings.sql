--------------------------------------------------------------------------------
-- HALOKAZI - job seeker settings (additive migration, safe to re-run)
--
-- The job seeker Settings screen shows date of birth and gender, and lets the user choose
-- notification and privacy preferences. 01_schema.sql has none of these, so this adds them
-- to job_seeker_profile. Every column is optional or has a default, so existing rows and
-- older application builds keep working.
--
--   date_of_birth, gender           - shown under Account Information (filled by the profile editor)
--   notify_email                    - important account updates by email
--   notify_application_updates      - application status changes
--   notify_newsletter               - career tips and platform news
--   (job alerts already exist as receive_job_alerts)
--   profile_visible                 - employers may discover the profile
--   show_email, show_phone          - employers may see contact details on the profile
--
-- Run after 01 -> 02 -> 03, as the schema owner.
--------------------------------------------------------------------------------
SET SERVEROUTPUT ON

DECLARE
    PROCEDURE add_column(p_column VARCHAR2, p_definition VARCHAR2) IS
        v_exists NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_exists
          FROM user_tab_columns
         WHERE table_name = 'JOB_SEEKER_PROFILE' AND column_name = UPPER(p_column);
        IF v_exists = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE job_seeker_profile ADD (' || p_column || ' ' || p_definition || ')';
            DBMS_OUTPUT.PUT_LINE('Added job_seeker_profile.' || p_column);
        END IF;
    END;

    PROCEDURE add_check(p_name VARCHAR2, p_condition VARCHAR2) IS
        v_exists NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_exists FROM user_constraints WHERE constraint_name = UPPER(p_name);
        IF v_exists = 0 THEN
            EXECUTE IMMEDIATE 'ALTER TABLE job_seeker_profile ADD CONSTRAINT ' || p_name || ' CHECK (' || p_condition || ')';
        END IF;
    END;
BEGIN
    add_column('date_of_birth',              'DATE');
    add_column('gender',                     'VARCHAR2(20)');
    add_column('notify_email',               'NUMBER(1) DEFAULT 1 NOT NULL');
    add_column('notify_application_updates', 'NUMBER(1) DEFAULT 1 NOT NULL');
    add_column('notify_newsletter',          'NUMBER(1) DEFAULT 0 NOT NULL');
    add_column('profile_visible',            'NUMBER(1) DEFAULT 1 NOT NULL');
    add_column('show_email',                 'NUMBER(1) DEFAULT 0 NOT NULL');
    add_column('show_phone',                 'NUMBER(1) DEFAULT 0 NOT NULL');

    add_check('ck_jsp_gender', 'gender IS NULL OR gender IN (''MALE'', ''FEMALE'', ''OTHER'', ''PREFER_NOT_TO_SAY'')');
    add_check('ck_jsp_settings_flags',
              'notify_email IN (0, 1) AND notify_application_updates IN (0, 1) AND notify_newsletter IN (0, 1)'
              || ' AND profile_visible IN (0, 1) AND show_email IN (0, 1) AND show_phone IN (0, 1)');
END;
/
