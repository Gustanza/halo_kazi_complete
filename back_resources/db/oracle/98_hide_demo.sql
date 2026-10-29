--------------------------------------------------------------------------------
-- HALOKAZI - take the 04_seed_demo.sql data off the public board.
--
-- Demo rows cannot be physically deleted: vacancy_status_history is append-only. Instead this
-- uses the normal lifecycle: demo vacancies move to DELETED and demo employer accounts to
-- DEACTIVATED, which removes them from every public endpoint and from the stats.
--------------------------------------------------------------------------------
UPDATE vacancy
   SET status = 'DELETED'
 WHERE status IN ('DRAFT', 'ACTIVE', 'CLOSED')
   AND employer_id IN (SELECT e.employer_id FROM employer e JOIN app_user u ON u.user_id = e.user_id
                        WHERE u.email LIKE 'demo+%@halokazi.test');

UPDATE app_user
   SET account_status = 'DEACTIVATED'
 WHERE email LIKE 'demo+%@halokazi.test'
   AND account_status = 'ACTIVE';

COMMIT;
