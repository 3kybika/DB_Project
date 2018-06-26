--users:
DROP INDEX IF EXISTS  users_id_to_nickname_idx;
CREATE INDEX IF NOT EXISTS users_id_to_nickname_idx ON users (id, nickname DESC);

DROP INDEX IF EXISTS users_nickname;
CREATE INDEX users_nickname ON  users(nickname, id);

DROP INDEX IF EXISTS users_cluster_idx ;
CREATE INDEX users_cluster_idx ON users (id, nickname, email, fullname, about);
CLUSTER users USING users_cluster_idx;

--forums_users
DROP INDEX IF EXISTS  forums_users_of_forum_id_nickname_idx;
CREATE INDEX IF NOT EXISTS forums_users_of_forum_id_nickname_idx ON forums_users(forum_id, nickname);

-- posts:
DROP INDEX IF EXISTS  posts_id_idx;
CREATE INDEX posts_id_idx ON posts (id);

DROP INDEX IF EXISTS  posts_of_root_post_path_idx;
CREATE INDEX posts_of_root_post_path_idx ON posts(root_post, path);

DROP INDEX IF EXISTS  posts_on_thread_id_created_id;
CREATE INDEX posts_on_thread_id_created_id ON posts(thread_id, created, id);

DROP INDEX IF EXISTS posts_of_thread_id_id_idx;
CREATE INDEX posts_of_thread_id_id_idx ON posts(thread_id, id)
WHERE parent = 0;

--forums:
DROP INDEX IF EXISTS  forums_slug_id_idx;
CREATE UNIQUE INDEX forums_slug_id_idx ON forums (slug, id);

-- threads:
DROP INDEX IF EXISTS  thread_slug_id_idx;
CREATE UNIQUE INDEX thread_slug_id_idx ON threads (slug, id);

DROP INDEX IF EXISTS  thread_forum_created_idx;
CREATE INDEX thread_forum_created_idx  ON threads (forum_id, created);
