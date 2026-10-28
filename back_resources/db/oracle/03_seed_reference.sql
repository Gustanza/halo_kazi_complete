--------------------------------------------------------------------------------
-- HALOKAZI - reference / configuration seed data (Oracle 19c)
-- Run after 02_logic.sql. Swahili labels should be reviewed by a native speaker
-- before go-live. Districts are not seeded here (load the official list separately).
--------------------------------------------------------------------------------
SET DEFINE OFF

--------------------------------------------------------------------------------
-- System settings (single place to change business values)
--------------------------------------------------------------------------------
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('PROFILE_MIN_COMPLETION', '75', 'NUMBER', 'Minimum profile completion % required to apply (APP-01)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('OTP_TTL_SECONDS', '120', 'NUMBER', 'SMS OTP validity (AUTH-03)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('OTP_MAX_RESENDS', '3', 'NUMBER', 'Max OTP resends within the control window (AUTH-03)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('OTP_RESEND_WINDOW_MINUTES', '30', 'NUMBER', 'Control window for OTP resends - TO CONFIRM');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('SESSION_IDLE_TIMEOUT_MINUTES', '10', 'NUMBER', 'Session inactivity timeout (AUTH-12)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('DRAFT_VACANCY_EXPIRY_DAYS', '14', 'NUMBER', 'Unpublished drafts expire after N days (VAC-02)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('PASSWORD_MIN_LENGTH', '8', 'NUMBER', 'Minimum password length (AUTH-06)');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('LOGIN_MAX_FAILED_ATTEMPTS', '5', 'NUMBER', 'Failed logins before temporary lock - TO CONFIRM');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('LOGIN_LOCK_MINUTES', '15', 'NUMBER', 'Temporary lock duration - TO CONFIRM');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('UPLOAD_MAX_SIZE_MB', '5', 'NUMBER', 'Max upload size per file - TO CONFIRM');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('UPLOAD_ALLOWED_TYPES', 'application/pdf,image/jpeg,image/png', 'STRING', 'Allowed upload MIME types');
INSERT INTO system_setting (setting_key, setting_value, value_type, description) VALUES
    ('DISPLAY_TIME_ZONE', 'Africa/Dar_es_Salaam', 'STRING', 'All timestamps shown in EAT (UTC+3)');

--------------------------------------------------------------------------------
-- Profile completion weights (Requirements section 5) - total 100
--------------------------------------------------------------------------------
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('CONTACT',         'Contact Information',  'Taarifa za Mawasiliano', 10, 0, 1);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('EDUCATION',       'Education',            'Elimu',                  15, 0, 2);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('WORK_EXPERIENCE', 'Work Experience',      'Uzoefu wa Kazi',         15, 1, 3);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('SKILLS',          'Skills',               'Ujuzi',                  15, 0, 4);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('CERTIFICATIONS',  'Certifications',       'Vyeti',                  10, 1, 5);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('LANGUAGES',       'Languages',            'Lugha',                  10, 0, 6);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('ADDRESS',         'Address/Location',     'Anwani/Mahali',          10, 0, 7);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('SUMMARY',         'Professional Summary', 'Muhtasari wa Kitaaluma', 10, 0, 8);
INSERT INTO profile_section (section_code, name_en, name_sw, weight, can_be_na, sort_order) VALUES
    ('PROFILE_PICTURE', 'Profile Picture',      'Picha ya Wasifu',         5, 0, 9);

--------------------------------------------------------------------------------
-- Regions of Tanzania (26 Mainland + 5 Zanzibar)
--------------------------------------------------------------------------------
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('ARUSHA',        'Arusha',        'Arusha',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('DAR_ES_SALAAM', 'Dar es Salaam', 'Dar es Salaam', 'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('DODOMA',        'Dodoma',        'Dodoma',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('GEITA',         'Geita',         'Geita',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('IRINGA',        'Iringa',        'Iringa',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('KAGERA',        'Kagera',        'Kagera',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('KATAVI',        'Katavi',        'Katavi',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('KIGOMA',        'Kigoma',        'Kigoma',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('KILIMANJARO',   'Kilimanjaro',   'Kilimanjaro',   'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('LINDI',         'Lindi',         'Lindi',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MANYARA',       'Manyara',       'Manyara',       'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MARA',          'Mara',          'Mara',          'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MBEYA',         'Mbeya',         'Mbeya',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MOROGORO',      'Morogoro',      'Morogoro',      'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MTWARA',        'Mtwara',        'Mtwara',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('MWANZA',        'Mwanza',        'Mwanza',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('NJOMBE',        'Njombe',        'Njombe',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('PWANI',         'Pwani (Coast)', 'Pwani',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('RUKWA',         'Rukwa',         'Rukwa',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('RUVUMA',        'Ruvuma',        'Ruvuma',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('SHINYANGA',     'Shinyanga',     'Shinyanga',     'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('SIMIYU',        'Simiyu',        'Simiyu',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('SINGIDA',       'Singida',       'Singida',       'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('SONGWE',        'Songwe',        'Songwe',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('TABORA',        'Tabora',        'Tabora',        'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('TANGA',         'Tanga',         'Tanga',         'MAINLAND');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('UNGUJA_NORTH',  'Unguja North',  'Kaskazini Unguja', 'ZANZIBAR');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('UNGUJA_SOUTH',  'Unguja South',  'Kusini Unguja',    'ZANZIBAR');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('URBAN_WEST',    'Urban West',    'Mjini Magharibi',  'ZANZIBAR');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('PEMBA_NORTH',   'Pemba North',   'Kaskazini Pemba',  'ZANZIBAR');
INSERT INTO region (code, name_en, name_sw, zone) VALUES ('PEMBA_SOUTH',   'Pemba South',   'Kusini Pemba',     'ZANZIBAR');

--------------------------------------------------------------------------------
-- Job categories
--------------------------------------------------------------------------------
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('ICT',              'Information Technology',       'TEHAMA',                          1);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('TELECOM',          'Telecommunications',           'Mawasiliano ya Simu',             2);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('FINANCE',          'Finance & Accounting',         'Fedha na Uhasibu',                3);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('SALES_MARKETING',  'Sales & Marketing',            'Mauzo na Masoko',                 4);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('CUSTOMER_SERVICE', 'Customer Service',             'Huduma kwa Wateja',               5);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('ENGINEERING',      'Engineering',                  'Uhandisi',                        6);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('HEALTH',           'Health & Medical',             'Afya na Tiba',                    7);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('EDUCATION',        'Education & Training',         'Elimu na Mafunzo',                8);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('ADMINISTRATION',   'Administration & Office',      'Utawala na Ofisi',                9);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('HUMAN_RESOURCES',  'Human Resources',              'Rasilimali Watu',                10);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('LEGAL',            'Legal',                        'Sheria',                         11);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('LOGISTICS',        'Logistics & Transport',        'Usafirishaji na Ugavi',          12);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('AGRICULTURE',      'Agriculture',                  'Kilimo',                         13);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('HOSPITALITY',      'Hospitality & Tourism',        'Ukarimu na Utalii',              14);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('CONSTRUCTION',     'Construction',                 'Ujenzi',                         15);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('MANUFACTURING',    'Manufacturing',                'Viwanda',                        16);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('NGO',              'NGO & Development',            'Mashirika Yasiyo ya Kiserikali', 17);
INSERT INTO job_category (code, name_en, name_sw, sort_order) VALUES ('OTHER',            'Other',                        'Mengineyo',                      99);

--------------------------------------------------------------------------------
-- Employment types
--------------------------------------------------------------------------------
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('FULL_TIME',  'Full-time',  'Muda Wote',            1);
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('PART_TIME',  'Part-time',  'Muda Nusu',            2);
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('CONTRACT',   'Contract',   'Mkataba',              3);
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('TEMPORARY',  'Temporary',  'Muda Mfupi',           4);
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('INTERNSHIP', 'Internship', 'Mafunzo kwa Vitendo',  5);
INSERT INTO employment_type (code, name_en, name_sw, sort_order) VALUES ('VOLUNTEER',  'Volunteer',  'Kujitolea',            6);

--------------------------------------------------------------------------------
-- Education levels (ordered for "minimum education" matching)
--------------------------------------------------------------------------------
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('PRIMARY',     'Primary Education',       'Elimu ya Msingi',          1);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('O_LEVEL',     'Secondary (O-Level)',     'Kidato cha Nne',           2);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('A_LEVEL',     'Advanced Secondary (A-Level)', 'Kidato cha Sita',     3);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('CERTIFICATE', 'Certificate',             'Astashahada',              4);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('DIPLOMA',     'Diploma',                 'Stashahada',               5);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('BACHELOR',    'Bachelor''s Degree',      'Shahada ya Kwanza',        6);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('POSTGRAD_DIPLOMA', 'Postgraduate Diploma', 'Stashahada ya Uzamili',  7);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('MASTER',      'Master''s Degree',        'Shahada ya Uzamili',       8);
INSERT INTO education_level (code, name_en, name_sw, sort_order) VALUES ('PHD',         'Doctorate (PhD)',         'Shahada ya Uzamivu',       9);

--------------------------------------------------------------------------------
-- Languages
--------------------------------------------------------------------------------
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('sw', 'Swahili',    'Kiswahili');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('en', 'English',    'Kiingereza');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('fr', 'French',     'Kifaransa');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('ar', 'Arabic',     'Kiarabu');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('zh', 'Chinese',    'Kichina');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('pt', 'Portuguese', 'Kireno');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('de', 'German',     'Kijerumani');
INSERT INTO spoken_language (iso_code, name_en, name_sw) VALUES ('es', 'Spanish',    'Kihispania');

--------------------------------------------------------------------------------
-- Report reasons (moderation categories)
--------------------------------------------------------------------------------
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('FRAUD',                 'Fraud or scam',            'Udanganyifu au utapeli',      'ALL');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('FAKE_VACANCY',          'Fake vacancy',             'Nafasi ya kazi bandia',       'VACANCY');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('INAPPROPRIATE_CONTENT', 'Inappropriate content',    'Maudhui yasiyofaa',           'ALL');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('SPAM',                  'Spam',                     'Ujumbe taka',                 'ALL');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('ABUSIVE_BEHAVIOUR',     'Abusive behaviour',        'Tabia ya unyanyasaji',        'ALL');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('PAYMENT_REQUESTED',     'Asked for payment to apply', 'Kuombwa malipo ili kuomba kazi', 'EMPLOYER');
INSERT INTO report_reason (code, name_en, name_sw, applies_to) VALUES ('OTHER',                 'Other',                    'Nyingine',                    'ALL');

--------------------------------------------------------------------------------
-- Admin permissions (assigned individually by Super Admin)
--------------------------------------------------------------------------------
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('EMPLOYER_VERIFICATION_REVIEW', 'EMPLOYERS', 'Review employer verification', 'Kukagua uthibitisho wa waajiri', 'View, approve and reject employer verification submissions');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('USER_VIEW', 'USERS', 'View users', 'Kuona watumiaji', 'View job seeker and employer accounts');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('USER_SUSPEND', 'USERS', 'Suspend users', 'Kusimamisha watumiaji', 'Suspend and reactivate job seeker and employer accounts');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('VACANCY_MANAGE', 'VACANCIES', 'Manage vacancies', 'Kusimamia nafasi za kazi', 'View and close any vacancy');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('VACANCY_REOPEN_REVIEW', 'VACANCIES', 'Review reopen requests', 'Kukagua maombi ya kufungua upya', 'Approve or reject vacancy reopening requests');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('REPORT_MODERATE', 'MODERATION', 'Moderate user reports', 'Kushughulikia ripoti za watumiaji', 'Investigate, resolve and reject user reports');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('ANALYTICS_VIEW', 'REPORTS', 'View reports', 'Kuona ripoti', 'View authorised system reports');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('ANALYTICS_EXPORT', 'REPORTS', 'Export reports', 'Kupakua ripoti', 'Export reports to Excel/PDF');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('AUDIT_LOG_VIEW', 'AUDIT', 'View audit log', 'Kuona kumbukumbu za ukaguzi', 'View audit log entries');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('CV_TEMPLATE_MANAGE', 'CV', 'Manage CV templates', 'Kusimamia violezo vya CV', 'Create, edit and deactivate CV templates (CV-10)');
INSERT INTO admin_permission (code, module, name_en, name_sw, description) VALUES
    ('REFERENCE_DATA_MANAGE', 'SETTINGS', 'Manage reference data', 'Kusimamia orodha za mfumo', 'Maintain categories, skills, regions and other lists');

--------------------------------------------------------------------------------
-- CV templates (CV-02: 5 predefined). layout_definition filled by the dev team.
--------------------------------------------------------------------------------
INSERT INTO cv_template (code, name_en, name_sw, description_en, description_sw, sort_order) VALUES
    ('CLASSIC', 'Classic', 'Kawaida', 'Traditional single-column layout', 'Mpangilio wa kawaida wa safu moja', 1);
INSERT INTO cv_template (code, name_en, name_sw, description_en, description_sw, sort_order) VALUES
    ('MODERN', 'Modern', 'Kisasa', 'Two-column layout with sidebar', 'Mpangilio wa safu mbili wenye upande', 2);
INSERT INTO cv_template (code, name_en, name_sw, description_en, description_sw, sort_order) VALUES
    ('PROFESSIONAL', 'Professional', 'Kitaaluma', 'Formal layout for experienced candidates', 'Mpangilio rasmi kwa wenye uzoefu', 3);
INSERT INTO cv_template (code, name_en, name_sw, description_en, description_sw, sort_order) VALUES
    ('MINIMAL', 'Minimal', 'Rahisi', 'Clean, minimal design', 'Muundo safi na rahisi', 4);
INSERT INTO cv_template (code, name_en, name_sw, description_en, description_sw, sort_order) VALUES
    ('GRADUATE', 'Graduate', 'Mhitimu', 'Education-first layout for new graduates', 'Mpangilio unaoanza na elimu kwa wahitimu', 5);

--------------------------------------------------------------------------------
-- Notification templates (section 11). Placeholders: {name}, {vacancy_title}, {company}, {ref}, {otp}
--------------------------------------------------------------------------------
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('APPLICATION_SUBMITTED', 'IN_APP',
     'Application submitted', 'Ombi limewasilishwa',
     'Your application {ref} for {vacancy_title} at {company} has been submitted.',
     'Ombi lako {ref} la nafasi ya {vacancy_title} katika {company} limewasilishwa.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('APPLICATION_IN_REVIEW', 'IN_APP',
     'Application in review', 'Ombi linakaguliwa',
     'Your application for {vacancy_title} at {company} is now being reviewed.',
     'Ombi lako la nafasi ya {vacancy_title} katika {company} sasa linakaguliwa.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('APPLICATION_REJECTED', 'IN_APP',
     'Application update', 'Taarifa ya ombi',
     'Thank you for applying for {vacancy_title} at {company}. Unfortunately your application was not successful.',
     'Asante kwa kuomba nafasi ya {vacancy_title} katika {company}. Kwa bahati mbaya ombi lako halikufanikiwa.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('APPLICATION_SHORTLISTED', 'IN_APP',
     'Shortlisted for interview', 'Umechaguliwa kwa usaili',
     'Congratulations! You have been shortlisted for interview for {vacancy_title} at {company}. The employer will contact you directly.',
     'Hongera! Umechaguliwa kwa usaili wa nafasi ya {vacancy_title} katika {company}. Mwajiri atawasiliana nawe moja kwa moja.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('NEW_RELEVANT_VACANCY', 'IN_APP',
     'New job that matches you', 'Kazi mpya inayokufaa',
     'A new vacancy matching your profile was posted: {vacancy_title} at {company}.',
     'Nafasi mpya ya kazi inayolingana na wasifu wako imetangazwa: {vacancy_title} katika {company}.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('EMPLOYER_VERIFICATION_APPROVED', 'IN_APP',
     'Verification approved', 'Uthibitisho umeidhinishwa',
     'Your company {company} has been verified. You can now publish vacancies.',
     'Kampuni yako {company} imethibitishwa. Sasa unaweza kutangaza nafasi za kazi.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('EMPLOYER_VERIFICATION_REJECTED', 'IN_APP',
     'Verification rejected', 'Uthibitisho umekataliwa',
     'Your verification for {company} was rejected: {reason}. Please correct the information and resubmit.',
     'Uthibitisho wa {company} umekataliwa: {reason}. Tafadhali rekebisha taarifa na uwasilishe tena.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('VACANCY_REOPEN_APPROVED', 'IN_APP',
     'Reopen request approved', 'Ombi la kufungua upya limekubaliwa',
     'Your request to reopen {vacancy_title} was approved. The vacancy is active again.',
     'Ombi lako la kufungua upya nafasi ya {vacancy_title} limekubaliwa. Nafasi iko wazi tena.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('VACANCY_REOPEN_REJECTED', 'IN_APP',
     'Reopen request rejected', 'Ombi la kufungua upya limekataliwa',
     'Your request to reopen {vacancy_title} was rejected: {reason}.',
     'Ombi lako la kufungua upya nafasi ya {vacancy_title} limekataliwa: {reason}.');
INSERT INTO notification_template (template_code, channel, title_en, title_sw, body_en, body_sw) VALUES
    ('OTP_PHONE_VERIFICATION', 'SMS',
     'Halokazi verification code', 'Nambari ya uthibitisho ya Halokazi',
     'Your Halokazi verification code is {otp}. It expires in 2 minutes.',
     'Nambari yako ya uthibitisho ya Halokazi ni {otp}. Itaisha muda baada ya dakika 2.');

COMMIT;
