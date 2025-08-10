-- Удаление таблиц с учётом зависимостей
DROP TABLE IF EXISTS comments CASCADE;

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_on TIMESTAMP NOT NULL
)