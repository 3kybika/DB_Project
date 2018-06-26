DROP INDEX IF EXISTS posts_id_idx;
CREATE INDEX posts_id_idx ON posts (id, root_post);

DROP INDEX IF EXISTS posts_of_thread_id_parent_id_idx;
CREATE INDEX posts_of_thread_id_parent_id_idx ON posts(thread_id, id)
WHERE parent = 0;

DROP INDEX IF EXISTS posts_id_path_idx;
CREATE INDEX posts_id_path_idx ON posts (id, path);