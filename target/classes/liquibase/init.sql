CREATE TABLE notification_tasks
(
    id                     BIGSERIAL PRIMARY KEY,
    message                TEXT      NOT NULL,
    chat_id                BIGINT    NOT NULL,
    notification_date_time TIMESTAMP NOT NULL

);