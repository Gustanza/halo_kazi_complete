-- Query to discover existing cv-related tables
SELECT table_name FROM user_tables WHERE LOWER(table_name) LIKE '%cv%' ORDER BY table_name;
-- Query to discover all columns of any cv table
SELECT table_name, column_name, data_type, data_length, data_precision, data_scale, nullable, data_default
FROM user_tab_columns 
WHERE LOWER(table_name) LIKE '%cv%'
ORDER BY table_name, column_id;
-- Check constraints on cv tables
SELECT table_name, constraint_name, constraint_type, search_condition 
FROM user_constraints 
WHERE LOWER(table_name) LIKE '%cv%'
ORDER BY table_name, constraint_name;
-- Check indexes
SELECT index_name, table_name, column_name 
FROM user_ind_columns 
WHERE LOWER(table_name) LIKE '%cv%'
ORDER BY table_name, index_name;
