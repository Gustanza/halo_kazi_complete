--------------------------------------------------------------------------------
-- HALOKAZI - DEMO DATA (development / shared test database only, never production)
--
-- Gives the public job board something to show while the employer verification and
-- vacancy posting flows are still being built:
--   * 8 approved employers, each with an ACTIVE employer account
--   * 22 open vacancies across regions, job types, categories, salaries and experience levels,
--     published between today and 40 days ago
--   * 1 DRAFT vacancy, which must never appear on the public board
--
-- Demo accounts use phones +255 700 000 0xx and emails demo+<name>@halokazi.test. Their
-- password_hash is not a BCrypt hash, so nobody can log in as them.
--
-- Safe to re-run: it does nothing if the demo employers already exist.
-- To take the demo data off the public board, run 98_hide_demo.sql.
-- Run after 01 -> 02 -> 03, as the schema owner.
--------------------------------------------------------------------------------
SET SERVEROUTPUT ON

DECLARE
    -- Variables must be declared before the local subprograms.
    v_existing NUMBER;
    e_crdb NUMBER; e_nmb NUMBER; e_voda NUMBER; e_yas NUMBER;
    e_airtel NUMBER; e_halotel NUMBER; e_lodges NUMBER; e_afya NUMBER;

    FUNCTION fn_region(p_code VARCHAR2) RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT region_id INTO v FROM region WHERE code = p_code;
        RETURN v;
    END;

    FUNCTION fn_type(p_code VARCHAR2) RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT employment_type_id INTO v FROM employment_type WHERE code = p_code;
        RETURN v;
    END;

    FUNCTION fn_category(p_code VARCHAR2) RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT category_id INTO v FROM job_category WHERE code = p_code;
        RETURN v;
    END;

    FUNCTION fn_education(p_code VARCHAR2) RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT education_level_id INTO v FROM education_level WHERE code = p_code;
        RETURN v;
    END;

    -- Creates an ACTIVE employer account plus an APPROVED employer; returns employer_id.
    FUNCTION add_employer(p_seq NUMBER, p_key VARCHAR2, p_name VARCHAR2, p_category VARCHAR2,
                      p_region VARCHAR2, p_size VARCHAR2, p_about VARCHAR2) RETURN NUMBER IS
        -- Local functions cannot be called inside SQL, so resolve lookups first.
        v_category    NUMBER := fn_category(p_category);
        v_region      NUMBER := fn_region(p_region);
        v_user_id     NUMBER;
        v_employer_id NUMBER;
    BEGIN
        INSERT INTO app_user (full_name, email, phone, password_hash, role, account_status,
                              phone_verified, phone_verified_at)
        VALUES (p_name, 'demo+' || p_key || '@halokazi.test', '+2557000000' || LPAD(p_seq, 2, '0'),
                'DEMO-ACCOUNT-NO-LOGIN', 'EMPLOYER', 'ACTIVE', 1, SYSTIMESTAMP)
        RETURNING user_id INTO v_user_id;

        INSERT INTO employer (user_id, business_name, registration_number, tin, verification_status,
                              approved_at, category_id, company_description, company_email, region_id,
                              company_size)
        VALUES (v_user_id, p_name, 'DEMO-REG-' || LPAD(p_seq, 3, '0'), '9990000' || LPAD(p_seq, 2, '0'),
                'APPROVED', SYSTIMESTAMP, v_category, p_about,
                'demo+' || p_key || '@halokazi.test', v_region, p_size)
        RETURNING employer_id INTO v_employer_id;
        RETURN v_employer_id;
    END;

    -- Publishes a vacancy, then backdates published_at so "date posted" filters have something to find.
    PROCEDURE add_vacancy(p_employer NUMBER, p_title VARCHAR2, p_region VARCHAR2, p_type VARCHAR2,
                      p_category VARCHAR2, p_education VARCHAR2, p_years NUMBER, p_positions NUMBER,
                      p_salary_min NUMBER, p_salary_max NUMBER, p_posted_days_ago NUMBER,
                      p_deadline_days NUMBER, p_status VARCHAR2 DEFAULT 'ACTIVE') IS
        v_education NUMBER := fn_education(p_education);
        v_region    NUMBER := fn_region(p_region);
        v_type      NUMBER := fn_type(p_type);
        v_category  NUMBER := fn_category(p_category);
        v_id        NUMBER;
        v_creator   NUMBER;
    BEGIN
        SELECT user_id INTO v_creator FROM employer WHERE employer_id = p_employer;
        INSERT INTO vacancy (employer_id, job_title, job_description, job_requirements, qualifications,
                             min_education_level_id, work_experience, min_experience_years, region_id,
                             employment_type_id, number_of_positions, application_deadline, category_id,
                             salary_min, salary_max, salary_currency, salary_period, status, created_by)
        VALUES (p_employer, p_title,
                'We are looking for a motivated ' || p_title || ' to join our team. You will work with '
                    || 'colleagues across the business to deliver excellent results for our customers.',
                'Strong communication skills; ability to work under pressure; good computer skills; '
                    || 'fluency in English and Kiswahili.',
                'Relevant qualification in the field; professional certification is an added advantage.',
                v_education,
                CASE WHEN p_years = 0 THEN 'No experience required'
                     ELSE 'At least ' || p_years || ' year' || CASE WHEN p_years > 1 THEN 's' END
                          || ' of relevant experience' END,
                p_years, v_region, v_type, p_positions,
                SYSTIMESTAMP + NUMTODSINTERVAL(p_deadline_days, 'DAY'), v_category,
                p_salary_min, p_salary_max, 'TZS',
                CASE WHEN p_salary_min IS NOT NULL OR p_salary_max IS NOT NULL THEN 'MONTH' END,
                p_status, v_creator)
        RETURNING vacancy_id INTO v_id;

        IF p_status = 'ACTIVE' THEN
            UPDATE vacancy
               SET published_at = SYSTIMESTAMP - NUMTODSINTERVAL(p_posted_days_ago, 'DAY')
                                               - NUMTODSINTERVAL(MOD(v_id, 7), 'HOUR')
             WHERE vacancy_id = v_id;
        END IF;
    END;

BEGIN
    SELECT COUNT(*) INTO v_existing FROM app_user WHERE email LIKE 'demo+%@halokazi.test';
    IF v_existing > 0 THEN
        DBMS_OUTPUT.PUT_LINE('Demo data already present (' || v_existing || ' demo accounts). Nothing to do.');
        RETURN;
    END IF;

    e_crdb    := add_employer(1, 'crdb',    'CRDB Bank PLC',          'FINANCE',     'DAR_ES_SALAAM', '1000+',
                          'Leading commercial bank in Tanzania.');
    e_nmb     := add_employer(2, 'nmb',     'NMB Bank PLC',           'FINANCE',     'DAR_ES_SALAAM', '1000+',
                          'Bank with the widest branch network in Tanzania.');
    e_voda    := add_employer(3, 'vodacom', 'Vodacom Tanzania',       'TELECOM',     'DAR_ES_SALAAM', '1000+',
                          'Mobile network operator and M-Pesa provider.');
    e_yas     := add_employer(4, 'yas',     'Yas',                    'TELECOM',     'DAR_ES_SALAAM', '1000+',
                          'Mobile network and digital services company.');
    e_airtel  := add_employer(5, 'airtel',  'Airtel Tanzania',        'TELECOM',     'DAR_ES_SALAAM', '501-1000',
                          'Mobile network operator and Airtel Money provider.');
    e_halotel := add_employer(6, 'halotel', 'Halotel',                'TELECOM',     'DAR_ES_SALAAM', '1000+',
                          'Mobile network with nationwide rural coverage.');
    e_lodges  := add_employer(7, 'lodges',  'Kilimanjaro Lodges Ltd', 'HOSPITALITY', 'ARUSHA',        '51-200',
                          'Safari lodges in northern Tanzania.');
    e_afya    := add_employer(8, 'afya',    'Afya Bora Hospital',     'HEALTH',      'DODOMA',        '201-500',
                          'Private referral hospital.');

    --           employer   title                           region          type         category            education   yrs pos salary min/max         posted deadline
    add_vacancy(e_crdb,    'Digital Marketing Specialist',  'DAR_ES_SALAAM', 'FULL_TIME',  'SALES_MARKETING',  'BACHELOR', 3,  1,  1800000, 2500000,   0,  30);
    add_vacancy(e_crdb,    'Branch Operations Officer',     'MWANZA',        'FULL_TIME',  'FINANCE',          'BACHELOR', 2,  3,  1200000, 1600000,   3,  25);
    add_vacancy(e_crdb,    'Graduate Trainee',              'DAR_ES_SALAAM', 'INTERNSHIP', 'FINANCE',          'BACHELOR', 0,  10, 400000,  400000,    9,  20);
    add_vacancy(e_nmb,     'Relationship Manager',          'DAR_ES_SALAAM', 'FULL_TIME',  'FINANCE',          'BACHELOR', 5,  2,  3000000, 4500000,   1,  35);
    add_vacancy(e_nmb,     'Credit Analyst',                'ARUSHA',        'FULL_TIME',  'FINANCE',          'BACHELOR', 3,  1,  2000000, 2800000,   12, 28);
    add_vacancy(e_nmb,     'IT Support Technician',         'DODOMA',        'CONTRACT',   'ICT',              'DIPLOMA',  1,  2,  900000,  1200000,   20, 15);
    add_vacancy(e_voda,    'Customer Care Agent',           'DAR_ES_SALAAM', 'FULL_TIME',  'CUSTOMER_SERVICE', 'DIPLOMA',  0,  15, 600000,  800000,    0,  21);
    add_vacancy(e_voda,    'Network Engineer',              'DAR_ES_SALAAM', 'FULL_TIME',  'ENGINEERING',      'BACHELOR', 4,  2,  3500000, 5000000,   5,  40);
    add_vacancy(e_voda,    'M-Pesa Agent Supervisor',       'MBEYA',         'FULL_TIME',  'SALES_MARKETING',  'DIPLOMA',  2,  4,  1000000, 1400000,   35, 10);
    add_vacancy(e_yas,     'Sales Executive',               'DAR_ES_SALAAM', 'FULL_TIME',  'SALES_MARKETING',  'DIPLOMA',  1,  8,  700000,  1100000,   2,  30);
    add_vacancy(e_yas,     'Data Analyst',                  'DAR_ES_SALAAM', 'FULL_TIME',  'ICT',              'BACHELOR', 3,  1,  NULL,    NULL,      6,  30);
    add_vacancy(e_yas,     'Retail Shop Assistant',         'TANGA',         'PART_TIME',  'SALES_MARKETING',  'O_LEVEL',  0,  5,  300000,  450000,    15, 14);
    add_vacancy(e_airtel,  'Accountant',                    'DAR_ES_SALAAM', 'FULL_TIME',  'FINANCE',          'BACHELOR', 3,  2,  1500000, 2200000,   4,  30);
    add_vacancy(e_airtel,  'Field Sales Representative',    'MOROGORO',      'CONTRACT',   'SALES_MARKETING',  'DIPLOMA',  1,  6,  500000,  900000,    25, 20);
    add_vacancy(e_halotel, 'Software Developer',            'DAR_ES_SALAAM', 'FULL_TIME',  'ICT',              'BACHELOR', 2,  3,  2500000, 3500000,   0,  45);
    add_vacancy(e_halotel, 'Radio Network Technician',      'KIGOMA',        'FULL_TIME',  'ENGINEERING',      'DIPLOMA',  2,  2,  1100000, 1500000,   8,  30);
    add_vacancy(e_halotel, 'Customer Service Intern',       'URBAN_WEST',    'INTERNSHIP', 'CUSTOMER_SERVICE', 'DIPLOMA',  0,  4,  250000,  250000,    10, 18);
    add_vacancy(e_halotel, 'HR Business Partner',           'DAR_ES_SALAAM', 'FULL_TIME',  'HUMAN_RESOURCES',  'MASTER',   6,  1,  4000000, NULL,      40, 12);
    add_vacancy(e_lodges,  'Front Office Manager',          'ARUSHA',        'FULL_TIME',  'HOSPITALITY',      'DIPLOMA',  4,  1,  1300000, 1800000,   7,  25);
    add_vacancy(e_lodges,  'Chef de Partie',                'ARUSHA',        'TEMPORARY',  'HOSPITALITY',      'CERTIFICATE', 2, 2, NULL,   NULL,      18, 21);
    add_vacancy(e_afya,    'Registered Nurse',              'DODOMA',        'FULL_TIME',  'HEALTH',           'DIPLOMA',  1,  6,  900000,  1300000,   1,  30);
    add_vacancy(e_afya,    'Medical Officer',               'DODOMA',        'FULL_TIME',  'HEALTH',           'BACHELOR', 3,  2,  3200000, 4200000,   28, 30);
    -- Draft: must stay hidden from every public endpoint.
    add_vacancy(e_halotel, 'Unpublished Draft Role',        'DAR_ES_SALAAM', 'FULL_TIME',  'ICT',              'BACHELOR', 1,  1,  NULL,    NULL,      0,  30, 'DRAFT');

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Demo data loaded: 8 employers, 22 open vacancies, 1 draft.');
END;
/
