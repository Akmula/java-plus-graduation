-- Удаление таблиц с учётом зависимостей
DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS participation_requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);

-- Таблица категорий (для связей с событиями)
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- Таблица событий
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    annotation TEXT NOT NULL,
    description TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL,
    published_on TIMESTAMP,
    initiator_id BIGINT NOT NULL,
    location_lat FLOAT NOT NULL,
    location_lon FLOAT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT DEFAULT 0,
    request_moderation BOOLEAN DEFAULT TRUE,
    state TEXT NOT NULL
);

-- Таблица заявок на участие
CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    status TEXT NOT NULL,
    requester_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_on TIMESTAMP NOT NULL
);

-- Таблица подборок событий
CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    pinned BOOLEAN DEFAULT FALSE
);

-- Связующая таблица между подборками и событиями
CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Внешние ключи
ALTER TABLE participation_requests
ADD CONSTRAINT fk_participation_user
FOREIGN KEY (requester_id) REFERENCES users(id);

ALTER TABLE participation_requests
ADD CONSTRAINT fk_participation_event
FOREIGN KEY (event_id) REFERENCES events(id);

ALTER TABLE events
ADD CONSTRAINT fk_category
FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE events
ADD CONSTRAINT fk_initiator
FOREIGN KEY (initiator_id) REFERENCES users(id);

ALTER TABLE comments
ADD CONSTRAINT fk_comment_author
FOREIGN KEY (author_id) REFERENCES users(id);

ALTER TABLE comments
ADD CONSTRAINT fk_comment_event
FOREIGN KEY (event_id) REFERENCES events(id);