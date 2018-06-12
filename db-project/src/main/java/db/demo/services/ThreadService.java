package db.demo.services;

import db.demo.mappers.ThreadDBMapper;
import db.demo.mappers.ThreadMapper;
import db.demo.models.ForumDBModel;
import db.demo.models.ThreadDBModel;
import db.demo.utils.TimestampUtil;
import db.demo.views.ThreadModel;
import db.demo.views.ThreadUpdateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private JdbcTemplate jdbcTemplate;
    private ThreadMapper threadMapper = new ThreadMapper();
    private ThreadDBMapper threadDBMapper = new ThreadDBMapper();

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear(){
        jdbcTemplate.update("TRUNCATE TABLE threads CASCADE ;");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM threads", Integer.class);
    }
    public Integer createThread(ThreadDBModel thread) {
        int threadId =  jdbcTemplate.queryForObject(
                "INSERT INTO threads " +
                        "(author_id, author_nickname, created, message, title, forum_id, forum_slug, slug) " +
                        "VALUES (?,?::citext,?::TIMESTAMP WITH TIME ZONE,?,?,?,?::citext,?::citext) " +
                        "RETURNING id",
                Integer.class,
                thread.getAuthorId(),
                thread.getAuthorNick(),
                thread.getCreated(),
                thread.getMessage(),
                thread.getTitle(),
                thread.getForum(),
                thread.getForumSlug(),
                thread.getSlug()
        );
        jdbcTemplate.update(
                "UPDATE forums SET threads = threads+1 WHERE id = ?;",
                thread.getForum()
        );
        jdbcTemplate.update(
                "INSERT INTO forums_users(user_id, forum_id) " +
                        "VALUES (?, ?) " +
                        "ON CONFLICT (forum_id, user_id) DO NOTHING;",
                thread.getAuthorId(),
                thread.getForum()
        );

        return threadId;
    }

    public ThreadModel getThreadById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * " +
                        "FROM threads t " +
                        "WHERE id = ? " +
                        "LIMIT 1;",
                threadMapper,
                id
        );
    }

    public ThreadDBModel getThreadDBById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * " +
                        "FROM threads t " +
                        "WHERE id = ? " +
                        "LIMIT 1;",
                threadDBMapper,
                id
        );
    }

    public ThreadDBModel getThreadDBBySlug(String slug) {
        return jdbcTemplate.queryForObject(
                "SELECT * " +
                        "FROM threads t " +
                        "WHERE slug = ?::citext " +
                        "LIMIT 1;",
                threadDBMapper,
                slug
        );
    }

    public ThreadDBModel getThreadDBBySlugOrId(String slug_or_id){
        ThreadDBModel threadDB;
        try {
            int id = Integer.parseInt(slug_or_id);
            return getThreadDBById(id);
        } catch (Exception e) {
            try {
                return getThreadDBBySlug(slug_or_id);
            } catch (Exception ex){
                return null;
            }
        }
    }

    public ThreadModel getThreadBySlugOrId(String slug_or_id){
        ThreadModel thread;
        try {
            int id = Integer.parseInt(slug_or_id);
            return getThreadById(id);
        } catch (Exception e) {
            return getThreadBySlug(slug_or_id);
        }
    }

    public ThreadModel getThreadBySlug(String slug){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM threads t " +
                            "WHERE t.slug = ?::citext",
                    threadMapper,
                    slug
            );
        } catch(Exception e){
            return null;
        }
    }

    public List<ThreadModel> getThreadsByForumDB (ForumDBModel forumDB, int limit, String since, boolean desc){
        String query = "SELECT *" +
                "FROM threads t " +
                "WHERE t.forum_id = ? ";
        ArrayList params = new ArrayList() ;
        params.add(forumDB.getId());

        if (since!= null && !since.isEmpty()) {
            if (desc) {
                query += " AND created <= ?::timestamptz ";
            } else {
                query += " AND created >= ?::timestamptz ";
            }
            params.add(since);
        }
        if (desc) {
            query += " ORDER BY created DESC ";
        } else {
            query += " ORDER BY created ";
        }

        if (limit != 0) {
            query += " LIMIT ? ";
            params.add(limit);
        }
        query+=";";
        List<ThreadModel> result =  jdbcTemplate.query(
                query,
                threadMapper,
                params.toArray()
        );
        return result;
    }

    public ThreadModel updateThreadData(ThreadModel thread, ThreadUpdateModel newData) {
        String query = "UPDATE threads SET ";
        ArrayList params = new ArrayList();

        if (newData.getMessage() != null) {
            query += " message = ?,";

            params.add(newData.getMessage());
            thread.setMessage(newData.getMessage());
        }

        if (newData.getTitle() != null) {
            query += " title = ?,";
            params.add(newData.getTitle());
            thread.setTitle(newData.getTitle());
        }

        query = query.substring(0, query.length()-1);

        if(params.size() > 0) {
            query += " WHERE id = ?;";
            params.add(thread.getId());
            jdbcTemplate.update(
                    query,
                    params.toArray()
            );
        }

        return thread;
    }

}
