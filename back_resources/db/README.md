# Halokazi Database (Oracle 19c)

Database design for Halokazi, built from the approved Requirements baseline (12 Sep 2026) and the Stage 5 System Modeling document, with the corrections agreed on 8 Oct 2026.

## Files

| File | Purpose |
|---|---|
| `oracle/01_schema.sql` | Tables, constraints, indexes (55 tables) |
| `oracle/02_logic.sql` | Business-rule triggers, profile-completion function, lifecycle job, views |
| `oracle/03_seed_reference.sql` | Settings, profile weights, regions, categories, permissions, CV templates, notification texts |
| `oracle/04_seed_demo.sql` | Demo employers and open jobs for the public job board. Dev/test only, never production |
| `oracle/05_settings.sql` | Job seeker settings columns: date of birth, gender, notification and privacy choices. Safe to re-run |
| `oracle/98_hide_demo.sql` | Takes the 04 demo data off the public endpoints (rows can't be deleted) |
| `oracle/99_drop_all.sql` | Drops everything. Dev/test only |
| `halokazi.dbml` | Full model for dbdiagram.io (all 55 tables). Source for the diagrams below |
| `diagrams/00_overview.dbml` | 18 core entities, keys and status only. Use this for presentations |
| `diagrams/01..08_*.dbml` | One module per file, full detail. Grey tables are other modules, shown with their key only |

Run in order: `01` → `02` → `03` → `05` (plus `04` on development databases only). The schema user needs `CREATE TABLE, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE, CREATE TRIGGER, CREATE JOB`.

**Tested 27 Oct 2026** on Oracle AI Database 26ai Free (23.26.3) as user `HALOKAZI` in `FREEPDB1`. All three scripts ran with zero errors: 55 tables, 104 indexes, 13 triggers, 2 functions, 2 procedures and 5 views, all VALID; the scheduler job is enabled; 126 reference rows were loaded. The trigger logic has compiled but has not yet been exercised with test data. Not yet run on 19c.

**28-29 Oct 2026:** `04_seed_demo.sql` and `05_settings.sql` applied to the shared development database (VPS). `05` is required by the backend: it maps the new columns and starts with `ddl-auto=validate`.

## Design decisions

| Decision | Choice |
|---|---|
| Database | Oracle 19c. Booleans are `NUMBER(1)`; JSON is `CLOB ... IS JSON` |
| Profile threshold | 75%, stored in `system_setting.PROFILE_MIN_COMPLETION` |
| Application snapshot | JSON (`profile_snapshot`, `cv_snapshot`) **plus** the generated PDF (`cv_snapshot_file_id`) |
| Cover letter | **Required** (differs from APP-04, so update the requirements doc) |
| Certificates on application | Supported via `application_certificate`, with frozen copies of the certificate details |
| Reapplying | Blocked while a previous application is SUBMITTED / IN_REVIEW / SHORTLISTED; allowed after REJECTED |
| Lookups | Regions, districts, categories, skills, employment types and education levels are managed tables |
| Bilingual data | `name_en` / `name_sw` on system lists and notification templates; `app_user.preferred_language`; user-written content is stored as typed |
| Timestamps | `TIMESTAMP WITH TIME ZONE`, displayed in EAT (UTC+3) |
| Deletion | Soft delete + anonymisation for users, employers, CVs, certificates and vacancies; applications are never deleted |
| Files | Metadata only in `stored_file`; file bytes live in isolated object storage, with a malware scan status per file |

Renamed from the Stage 5 ERD: `USER` → `app_user` (reserved word), `LANGUAGE` → `spoken_language`, `REPORT` → `user_report` (moderation, kept separate from analytics reports).

## Where each rule is enforced

**DB** = constraint/index, **TRG** = trigger, **APP** = application code.

| Requirement | Enforcement |
|---|---|
| AUTH-02/03 SMS OTP, 3 resends; lifetime from `OTP_TTL_SECONDS` (5 min; the requirement says 2 - update it if 5 stays) | `otp_code` (hash only, `resend_count ≤ 3`); timing in APP using settings |
| AUTH-04 login by email or phone | DB unique on `LOWER(email)` and `phone` |
| AUTH-07/08/09 employer pending/rejected/resubmit | `employer.verification_status`; TRG blocks vacancy creation unless APPROVED + ACTIVE |
| AUTH-11 deletion / anonymisation | `account_deletion_request`, `deleted_at`, `anonymized_at` |
| AUTH-12 10-min idle sessions | `user_session.last_activity_at`; lifecycle job revokes idle sessions |
| AUTH-13 rate limits | `login_attempt`, `otp_code`, `password_reset_token` give counts; APP/gateway enforces |
| Profile weights + Not Applicable | `profile_section`, `profile_section_na`, function `hk_profile_completion()` |
| APP-01 ≥ 75% to apply | TRG computes the score live on every application insert |
| PROF-03/04, CV-09, APP-13 snapshots | Snapshot columns are NOT NULL; TRG makes them immutable |
| CV-01..05 multiple CVs, 5 templates, PDF | `cv`, `cv_template` (5 seeded), `cv_section`, `latest_pdf_file_id` |
| CV-07/08 one CV, unchangeable | `application.cv_id` NOT NULL; TRG blocks changes and checks ownership |
| VER-01..07 verification + history | `employer_verification` (+ `_doc`); one pending per employer; decisions final (TRG) |
| VER-10 locked company data | TRG on `employer` |
| VER-11 one account per company | DB unique on `employer.tin` and registration number |
| VAC-02 drafts expire after 14 days | `draft_expires_at` + lifecycle job |
| VAC-04 critical fields locked | TRG once applications exist |
| VAC-06 auto-close at deadline | Lifecycle job every 5 min; TRG also rejects late applications in between |
| VAC-09 future deadline, positions > 0 | TRG / DB check |
| VAC-10 reopen via approved request | `vacancy_reopen_request`; TRG blocks CLOSED→ACTIVE without approval |
| VAC-11 suspended employer | TRG on application insert; `v_open_vacancy` excludes them |
| APP-05/06 duplicates / reapply | Unique function-based index ignoring REJECTED rows |
| APP-07/08/09 edit window, no withdrawal | TRG: edits only while SUBMITTED and before the deadline; DELETE blocked |
| APP-11/12 status flow + immutable history | TRG checks transitions and writes `application_status_history` (append-only) |
| Section 9 rule-based matching | Preference tables + `vacancy_skill` + view `v_vacancy_profile_match` |
| Section 11 notifications, read/unread | `notification`, `notification_template` (EN/SW) |
| Section 12.1 admin permissions | TRG: only SUPER_ADMIN grants/revokes, only to ADMIN, never to self; history kept |
| Section 12.2 reporting | `user_report` with explicit target columns + CHECK |
| Section 12.3 reports + Excel/PDF | Views `v_employer_vacancy_stats`, `v_job_seeker_application_stats`; `report_export` |
| Section 13 audit log | `audit_log` append-only (TRG) |
| Section 14.1 consent, policy versions | `policy_document`, `policy_acceptance` |
| Section 14.2 file validation / scanning | `stored_file.mime_type`, `size_bytes`, `scan_status`; settings for limits |

## The app must still

- Write `audit_log` rows for important actions.
- Build and store the JSON + PDF snapshots at submission time.
- Call `hk_refresh_profile_completion(profile_id)` after any profile change, so the displayed percentage stays current.
- Set `status_changed_by` when changing an application or vacancy status.
- Copy the approved verification details onto `employer` when approving.
- Send notifications.
- Hash passwords, OTPs and tokens.
- Check admin permissions via `v_admin_effective_permission`.

## Open items to confirm

1. **Required certificates vs "Not Applicable".** If at least one certificate is mandatory, a seeker who marked Certifications as N/A can never apply. Suggest: required only when the vacancy asks for it. This is not enforced in the DB yet.
2. **Reopening deadline-closed vacancies.** VAC-10 covers manual closure only; the design requires an approved request for any CLOSED→ACTIVE.
3. **Values marked `TO CONFIRM` in settings:** OTP resend window (30 min), login lockout (5 tries / 15 min), upload limit (5 MB).
4. **Districts** are not seeded; load the official NBS list.
5. **Swahili labels** need a native-speaker review.
6. **Cloud file storage location** must satisfy Tanzanian data-storage rules (Legal/DPO).
7. **The first Super Admin** is created by a one-off deploy script (password hash generated by the app).
