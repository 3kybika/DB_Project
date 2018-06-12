CREATE EXTENSION IF NOT EXISTS citext;

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id          SERIAL    NOT NULL PRIMARY KEY,
  email       citext    NOT NULL UNIQUE,
  fullname    citext    NOT NULL,
  nickname    citext    NOT NULL UNIQUE,
  about       citext
);

DROP INDEX IF EXISTS  users_id_to_nickname_idx;
CREATE INDEX IF NOT EXISTS users_id_to_nickname_idx ON users (id, nickname DESC);

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

DROP INDEX IF EXISTS  posts_of_root_post_path_idx;
CREATE INDEX posts_of_root_post_path_idx ON posts(root_post, path);
DROP INDEX IF EXISTS  posts_of_thread_parent_id_idx;
CREATE INDEX posts_of_thread_parent_id_idx ON posts(thread_id, parent, id);

DROP TABLE IF EXISTS votes;
CREATE TABLE votes (
  user_id     INTEGER   REFERENCES users,
  user_nickname citext,
  thread_id   INTEGER   REFERENCES threads,
  voice       INTEGER,

  UNIQUE (user_id, thread_id)
);
--ToDo условие уникальности можно ли пропустить?
CREATE TABLE IF NOT EXISTS forums_users (
	user_id INTEGER REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  forum_id INTEGER REFERENCES forums(id) ON DELETE CASCADE NOT NULL,
	CONSTRAINT user_forum UNIQUE (user_id, forum_id)
);

DROP INDEX IF EXISTS  forums_users_of_user_id_idx;
CREATE INDEX IF NOT EXISTS forums_users_of_user_id_idx ON forums_users(user_id);
DROP INDEX IF EXISTS  forums_users_of_forum_id_idx;
CREATE INDEX IF NOT EXISTS forums_users_of_forum_id_idx ON forums_users(forum_id);
/*
-- На обновление
CREATE OR REPLACE FUNCTION updateVotes() RETURNS TRIGGER AS
$update_votes_trigger$
	BEGIN
		IF (NEW.vote!=OLD.vote)
		THEN
			IF (NEW.vote>0)
			THEN
			  UPDATE threads SET votes=votes+2 WHERE id=NEW.thread_id;
			ELSE
			  UPDATE threads SET votes=votes-2 WHERE id=NEW.thread_id;
			END IF;
		END IF;
		RETURN NEW;
	END;
$vote_update_trig$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_votes_trigger ON votes;
CREATE TRIGGER update_votes_trigger AFTER UPDATE ON votes FOR EACH ROW EXECUTE PROCEDURE updateVotes();
*/
