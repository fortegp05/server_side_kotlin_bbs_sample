INSERT INTO users(name, email, password, role)
SELECT * FROM (
 SELECT
  'admin',
  'test@example.com',
  '$2a$10$CPNJ.PlWH8k1aMhC6ytjIuwxYuLWKMXTP3H6h.LRnpumtccpvXEGy',
  'ADMIN'
) AS tbl
WHERE NOT EXISTS (SELECT 1 FROM users);