select *
from users;

select *
from notifications;

select *
from notification_logs;

-- DDL reference
-- ALTER TABLE users ADD COLUMN phone VARCHAR(20);
-- CREATE TABLE notification_logs (
--     id BIGSERIAL PRIMARY KEY,
--     notification_id BIGINT NOT NULL REFERENCES notifications(id),
--     channel INT NOT NULL,
--     status VARCHAR(20) NOT NULL,
--     detail TEXT,
--     sent_at TIMESTAMP NOT NULL
-- );
