CREATE EXTENSION IF NOT EXISTS citext;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id          SERIAL    NOT NULL PRIMARY KEY,
  email       citext    NOT NULL UNIQUE,
  fullname    citext    NOT NULL,
  nickname    citext    NOT NULL UNIQUE,
  about       citext
);

DROP TABLE IF EXISTS forums;
CREATE TABLE forums (
  id          SERIAL    NOT NULL PRIMARY KEY,
  slug        citext    NOT NULL UNIQUE,
  title       citext    NOT NULL,
  author_id   INTEGER   REFERENCES users,
  author_nickname citext,
  threads     INTEGER   DEFAULT 0,
  posts       INTEGER   DEFAULT 0
);

DROP TABLE IF EXISTS threads;
CREATE TABLE threads (
  id          SERIAL    NOT NULL PRIMARY KEY,
  author_id   INTEGER   REFERENCES users,
  author_nickname citext,
  created     TIMESTAMP WITH TIME ZONE,
  forum_id    INTEGER   REFERENCES forums,
  forum_slug  citext,
  message     citext    NOT NULL,
  slug        citext    UNIQUE,
  title       citext    NOT NULL,
  votes       INTEGER   DEFAULT 0
);

DROP TABLE IF EXISTS posts;
CREATE TABLE posts (
  id          SERIAL    NOT NULL PRIMARY KEY ,
  author_id   INTEGER   REFERENCES users,
  author_nickname citext,
  created     TIMESTAMP WITH TIME ZONE,
  forum_id    INTEGER   REFERENCES forums,
  forum_slug  citext,
  isEdited    BOOLEAN   DEFAULT FALSE,
  message     citext,
  parent      INTEGER   DEFAULT 0,
  thread_id   INTEGER   REFERENCES threads,
  path        INTEGER [],
  root_post   INTEGER
);

DROP TABLE IF EXISTS votes;
CREATE TABLE votes (
  user_id     INTEGER   REFERENCES users,
  user_nickname citext,
  thread_id   INTEGER   REFERENCES threads,
  voice       INTEGER,

  UNIQUE (user_id, thread_id)
);

CREATE TABLE IF NOT EXISTS forums_users (
  user_id     INTEGER,
  email       citext,
  fullname    citext,
  nickname    citext,
  about       citext,
  forum_id INTEGER,
  UNIQUE (forum_id, user_id)
);

--- Для обновления авторов из тредов
/*
CREATE OR REPLACE FUNCTION adding_authors_From_justcreated_thread_proc()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  INSERT INTO forums_users(user_id, nickname, fullname, email, about, forum_id)
    (SELECT
       new.author_id,
       u.nickname,
       u.fullname,
       u.email,
       u.about,
       new.forum_id
     FROM users u
     WHERE u.id = new.author_id)
  ON CONFLICT DO NOTHING;
  RETURN new;
END;
$$;

DROP TRIGGER IF EXISTS adding_authors_From_justcreated_thread
ON threads;

CREATE TRIGGER adding_authors_From_justcreated_thread
BEFORE INSERT
  ON threads
FOR EACH ROW
EXECUTE PROCEDURE adding_authors_From_justcreated_thread_proc();

--- Для обновления авторов из поста

CREATE OR REPLACE FUNCTION adding_authors_From_justcreated_posts_proc()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  INSERT INTO forums_users(user_id, nickname, fullname, email, about, forum_id)
    (SELECT
       new.author_id,
       u.nickname,
       u.fullname,
       u.email,
       u.about,
       new.forum_id
     FROM users u
     WHERE u.id = new.author_id)
  ON CONFLICT DO NOTHING;
  RETURN new;
END;
$$;

DROP TRIGGER IF EXISTS adding_authors_From_justcreated_post
ON posts;

CREATE TRIGGER adding_authors_From_justcreated_post
BEFORE INSERT
  ON posts
FOR EACH ROW
EXECUTE PROCEDURE adding_authors_From_justcreated_posts_proc();*/