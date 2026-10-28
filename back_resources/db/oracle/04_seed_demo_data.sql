--------------------------------------------------------------------------------
-- HALOKAZI - Demo Seed Data for Testing & Postman
--------------------------------------------------------------------------------
SET DEFINE OFF;

-- Clean existing demo data if any
DELETE FROM notification WHERE user_id IN (1, 100);
DELETE FROM cv WHERE profile_id = 1;
DELETE FROM profile_section_na WHERE profile_id = 1;
DELETE FROM profile_skill WHERE profile_id = 1;
DELETE FROM profile_language WHERE profile_id = 1;
DELETE FROM work_experience WHERE profile_id = 1;
DELETE FROM education WHERE profile_id = 1;
DELETE FROM job_seeker_profile WHERE user_id = 1;
DELETE FROM vacancy WHERE vacancy_id IN (1, 2, 3);
DELETE FROM employer WHERE employer_id = 10;
DELETE FROM app_user WHERE user_id IN (1, 100);

-- 1. Create Employer User & Approved Employer
INSERT INTO app_user (
    user_id, full_name, email, phone, password_hash, role,
    account_status, phone_verified, phone_verified_at, preferred_language, created_at, updated_at
) VALUES (
    100, 'Halotel HR', 'hr@halotel.co.tz', '+255620000100',
    '$2a$10$abcdefghijklmnopqrstuvwxyzA1B2C3D4E5F6G7H8I9J0',
    'EMPLOYER', 'ACTIVE', 1, SYSTIMESTAMP, 'EN', SYSTIMESTAMP, SYSTIMESTAMP
);

INSERT INTO employer (
    employer_id, user_id, business_name, registration_number, tin,
    verification_status, approved_at, category_id, company_description,
    website, company_email, company_phone, region_id, company_size, created_at, updated_at
) VALUES (
    10, 100, 'Halotel Tanzania PLC', 'REG-123456', '123456789',
    'APPROVED', SYSTIMESTAMP, 2, 'Halotel is a leading telecommunications company operating across Tanzania.',
    'https://www.halotel.co.tz', 'careers@halotel.co.tz', '+255620000100', 1, '501-1000', SYSTIMESTAMP, SYSTIMESTAMP
);

-- 2. Create Active Vacancies
INSERT INTO vacancy (
    vacancy_id, employer_id, job_title, job_description, job_requirements, qualifications,
    work_experience, min_experience_years, region_id, employment_type_id, number_of_positions,
    application_deadline, category_id, salary_min, salary_max, salary_currency, salary_period,
    status, published_at, created_by, created_at, updated_at
) VALUES (
    1, 10, 'Senior Java Backend Engineer',
    'We are looking for a Senior Java Backend Engineer to design and implement robust APIs and microservices for our core enterprise platforms.',
    'Proficient in Java 17+, Spring Boot, Spring Data JPA, Oracle Database, and RESTful architecture.',
    'BSc in Computer Science, Software Engineering, or equivalent experience.',
    'At least 3 years of production backend experience', 3.0, 1, 1, 2,
    SYSTIMESTAMP + NUMTODSINTERVAL(30, 'DAY'), 2, 2500000, 4000000, 'TZS', 'MONTH',
    'ACTIVE', SYSTIMESTAMP - NUMTODSINTERVAL(2, 'DAY'), 100, SYSTIMESTAMP - NUMTODSINTERVAL(2, 'DAY'), SYSTIMESTAMP
);

INSERT INTO vacancy (
    vacancy_id, employer_id, job_title, job_description, job_requirements, qualifications,
    work_experience, min_experience_years, region_id, employment_type_id, number_of_positions,
    application_deadline, category_id, salary_min, salary_max, salary_currency, salary_period,
    status, published_at, created_by, created_at, updated_at
) VALUES (
    2, 10, 'Digital Marketing Specialist',
    'Drive online growth, campaign execution, and social media engagement for consumer digital products.',
    'Experience in SEO, SEM, content strategy, and social advertising.',
    'Diploma or Degree in Marketing, Business, or Communications.',
    '2 years experience in digital marketing', 2.0, 1, 1, 1,
    SYSTIMESTAMP + NUMTODSINTERVAL(20, 'DAY'), 4, 1500000, 2500000, 'TZS', 'MONTH',
    'ACTIVE', SYSTIMESTAMP - NUMTODSINTERVAL(1, 'DAY'), 100, SYSTIMESTAMP - NUMTODSINTERVAL(1, 'DAY'), SYSTIMESTAMP
);

INSERT INTO vacancy (
    vacancy_id, employer_id, job_title, job_description, job_requirements, qualifications,
    work_experience, min_experience_years, region_id, employment_type_id, number_of_positions,
    application_deadline, category_id, salary_min, salary_max, salary_currency, salary_period,
    status, published_at, created_by, created_at, updated_at
) VALUES (
    3, 10, 'Customer Care Agent',
    'Provide top-tier telephone and digital chat customer support to Halotel subscribers across the country.',
    'Fluent in both English and Kiswahili. Strong communication and problem-solving skills.',
    'Certificate or Diploma in any field.',
    '1 year customer care or sales experience', 1.0, 1, 1, 5,
    SYSTIMESTAMP + NUMTODSINTERVAL(14, 'DAY'), 9, 800000, 1200000, 'TZS', 'MONTH',
    'ACTIVE', SYSTIMESTAMP, 100, SYSTIMESTAMP, SYSTIMESTAMP
);

-- 3. Create Job Seeker User & Profile (Raymond Mwangi)
INSERT INTO app_user (
    user_id, full_name, email, phone, password_hash, role,
    account_status, phone_verified, phone_verified_at, preferred_language, created_at, updated_at
) VALUES (
    1, 'Raymond Mwangi', 'raymond@halotel.co.tz', '+255620000001',
    '$2a$10$abcdefghijklmnopqrstuvwxyzA1B2C3D4E5F6G7H8I9J0',
    'JOB_SEEKER', 'ACTIVE', 1, SYSTIMESTAMP, 'EN', SYSTIMESTAMP, SYSTIMESTAMP
);

INSERT INTO job_seeker_profile (
    profile_id, user_id, contact_email, alternate_phone, region_id,
    headline, professional_summary, expected_salary_min, receive_job_alerts,
    completion_percentage, created_at, updated_at
) VALUES (
    1, 1, 'raymond@halotel.co.tz', '+255620000001', 1,
    'Full Stack Software Engineer',
    'Passionate engineer with experience in Spring Boot, Oracle, and modern web application development.',
    2000000, 1, 85, SYSTIMESTAMP, SYSTIMESTAMP
);

-- Education (15%)
INSERT INTO education (
    education_id, profile_id, education_level_id, institution_name, qualification, field_of_study,
    start_date, is_current, created_at, updated_at
) VALUES (
    1, 1, 4, 'University of Dar es Salaam', 'BSc in Computer Science', 'Computer Science',
    TO_DATE('2018-09-01', 'YYYY-MM-DD'), 0, SYSTIMESTAMP, SYSTIMESTAMP
);

-- Work Experience (15%)
INSERT INTO work_experience (
    work_experience_id, profile_id, company_name, job_title, start_date, is_current,
    description, created_at, updated_at
) VALUES (
    1, 1, 'Tech Solutions Ltd', 'Software Developer',
    TO_DATE('2021-01-01', 'YYYY-MM-DD'), 1,
    'Built backend REST APIs and database integrations.', SYSTIMESTAMP, SYSTIMESTAMP
);

-- Skills (15%)
MERGE INTO skill s USING (SELECT 'Java' AS n, 'Java' AS sw, 2 AS cat FROM dual) d
ON (LOWER(s.name_en) = LOWER(d.n))
WHEN NOT MATCHED THEN INSERT (name_en, name_sw, category_id, is_active) VALUES (d.n, d.sw, d.cat, 1);

INSERT INTO profile_skill (profile_id, skill_id)
SELECT 1, skill_id FROM skill WHERE LOWER(name_en) = 'java';

-- Spoken languages (10%)
MERGE INTO spoken_language s USING (SELECT 'sw' AS iso, 'Swahili' AS en, 'Kiswahili' AS sw FROM dual) d
ON (s.iso_code = d.iso)
WHEN NOT MATCHED THEN INSERT (iso_code, name_en, name_sw, is_active) VALUES (d.iso, d.en, d.sw, 1);

INSERT INTO profile_language (profile_id, language_id, proficiency)
SELECT 1, language_id, 'NATIVE' FROM spoken_language WHERE iso_code = 'sw';

-- Certifications (Marked NA: 10%)
INSERT INTO profile_section_na (profile_id, section_code) VALUES (1, 'CERTIFICATIONS');

-- 4. Create Initial CV for Job Seeker
INSERT INTO cv (
    cv_id, profile_id, template_id, cv_name, cv_language, created_at, updated_at
) VALUES (
    1, 1, 1, 'Raymond Professional CV', 'EN', SYSTIMESTAMP, SYSTIMESTAMP
);

-- 5. Create Sample Notifications
INSERT INTO notification (
    notification_id, user_id, title, message, related_entity_type, related_entity_id,
    is_read, created_at
) VALUES (
    1, 1, 'Welcome to Halokazi!',
    'Your account is active. Explore vacancies and build your CV today.',
    'VACANCY', 1, 0, SYSTIMESTAMP
);

INSERT INTO notification (
    notification_id, user_id, title, message, related_entity_type, related_entity_id,
    is_read, created_at
) VALUES (
    2, 1, 'New Vacancy Matched',
    'Halotel Tanzania PLC posted a new vacancy: Senior Java Backend Engineer.',
    'VACANCY', 1, 0, SYSTIMESTAMP
);

COMMIT;
