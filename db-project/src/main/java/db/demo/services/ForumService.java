package db.demo.services;

import db.demo.mappers.ForumDBMapper;
import db.demo.mappers.ForumMapper;
import db.demo.mappers.UserMapper;
import db.demo.models.ForumDBModel;
import db.demo.views.ForumModel;
import db.demo.views.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForumService {

    private JdbcTemplate jdbcTemplate;
    private static ForumMapper forumMapper = new ForumMapper();
    private static ForumDBMapper forumDBMapper = new ForumDBMapper();
    private static UserMapper userMapper = new UserMapper();
    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear(){
        jdbcTemplate.update("TRUNCATE TABLE forums CASCADE; ");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM forums", Integer.class);
    }

    public Integer createForum(ForumDBModel forum) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO forums (slug, title, author_id, author_nickname ) " +
                        "VALUES(?,?,?,?) " +
                        "RETURNING id ;",
                Integer.class,
                forum.getSlug(),
                forum.getTitle(),
                forum.getUserId(),
                forum.getUserNickame()
        );
    }

    public ForumModel getForumById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT *" +
                            "FROM forums  " +
                            "WHERE id = ? " +
                            "LIMIT 1;",
                    forumMapper,
                    id
            );
        } catch (Exception e) {
            return null;
        }
    }

    public ForumModel getForumBySlug(String slug) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT *" +
                            "FROM forums  f " +
                            "WHERE f.slug = ?::citext " +
                            "LIMIT 1;",
                    forumMapper,
                    slug
            );
        } catch (Exception e) {
            return null;
        }
    }

    public ForumDBModel getForumDBBySlug(String slug){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM forums f " +
                            "WHERE slug = ?::citext " +
                            "LIMIT 1;",
                    forumDBMapper,
                    slug
            );
        } catch (Exception e){
            return null;
        }
    }

    public List<UserModel> getUsersOfForum(int forumId, int limit, String since, boolean desc) {

        String query =
                "SELECT * " +
                        "FROM users WHERE id IN " +
                        "(SELECT user_id FROM forums_users WHERE forum_id=?) ";
        ArrayList params = new ArrayList() ;
        params.add(forumId);

        if (since!= null && !since.isEmpty()) {
            if (desc) {
                query += " AND nickname < ? ";
            } else {
                query += " AND nickname > ? ";
            }
            params.add(since);
        }

        if (desc) {
            query += " ORDER BY nickname DESC ";
        } else {
            query += " ORDER BY nickname ASC ";
        }

        if (limit != 0) {
            query += " LIMIT ?;";
            params.add(limit);
        }

        return jdbcTemplate.query(
                query,
                params.toArray(),
                new UserMapper()
        );
    }

}
