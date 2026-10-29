-- =============================================================================
-- Proposed Schema Changes for CV Builder & My CVs Feature
-- =============================================================================
-- Target: Oracle FREEPDB1 (halokazi schema)
-- Status: PROPOSAL — do NOT run until reviewed and approved by the team.
--
-- The existing CV, CV_SECTION, and CV_TEMPLATE tables are already in place.
-- These ALTER statements add columns needed for the full CV Builder workflow.
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1. Add STATUS column to CV table (DRAFT / COMPLETED lifecycle)
-- ---------------------------------------------------------------------------
ALTER TABLE cv ADD (
    status VARCHAR2(20) DEFAULT 'DRAFT' NOT NULL
);
ALTER TABLE cv ADD CONSTRAINT ck_cv_status CHECK (status IN ('DRAFT', 'COMPLETED'));

-- ---------------------------------------------------------------------------
-- 2. Add IS_DEFAULT flag to CV table (one default CV per profile)
-- ---------------------------------------------------------------------------
ALTER TABLE cv ADD (
    is_default NUMBER(1) DEFAULT 0 NOT NULL
);
ALTER TABLE cv ADD CONSTRAINT ck_cv_is_default CHECK (is_default IN (0, 1));

-- ---------------------------------------------------------------------------
-- 3. Add LAST_STEP column to CV table (tracks builder wizard progress)
-- ---------------------------------------------------------------------------
ALTER TABLE cv ADD (
    last_step VARCHAR2(30)
);
ALTER TABLE cv ADD CONSTRAINT ck_cv_last_step CHECK (
    last_step IS NULL OR last_step IN (
        'PERSONAL_INFO', 'EXPERIENCE', 'EDUCATION', 'SKILLS',
        'CERTIFICATIONS', 'LANGUAGES', 'PROJECTS', 'REFERENCES', 'TEMPLATE'
    )
);

-- ---------------------------------------------------------------------------
-- 4. Add VERSION column for optimistic locking
-- ---------------------------------------------------------------------------
ALTER TABLE cv ADD (
    version NUMBER(10) DEFAULT 1 NOT NULL
);

-- ---------------------------------------------------------------------------
-- 5. Index on profile_id for efficient per-jobseeker queries
--    (check if it already exists before running)
-- ---------------------------------------------------------------------------
-- CREATE INDEX idx_cv_profile_id ON cv (profile_id);

-- ---------------------------------------------------------------------------
-- 6. Unique constraint: only one default CV per profile
--    Uses a function-based index that only indexes rows where is_default = 1
-- ---------------------------------------------------------------------------
-- CREATE UNIQUE INDEX uq_cv_default_per_profile
--     ON cv (CASE WHEN is_default = 1 AND deleted_at IS NULL THEN profile_id END);

-- ---------------------------------------------------------------------------
-- NOTES:
-- - No new tables needed; all snapshot data is managed in the application
--   layer (in-memory DataStore) for the current phase.
-- - If/when snapshot tables (cv_experience, cv_education, etc.) are needed
--   for persistence, a separate migration script will be provided.
-- - The cv_template table already has 5 rows: CLASSIC, MODERN, PROFESSIONAL,
--   MINIMAL, GRADUATE.
-- =============================================================================
