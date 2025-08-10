-- Удаление таблиц с учётом зависимостей
DROP TABLE IF EXISTS users CASCADE;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);