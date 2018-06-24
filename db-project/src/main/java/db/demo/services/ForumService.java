package db.demo.services;

import db.demo.mappers.ForumDBMapper;
import db.demo.mappers.ForumMapper;
import db.demo.mappers.UserMapper;
import db.demo.models.ForumDBModel;
import db.demo.views.ForumModel;
import db.demo.views.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
                            "WHERE f.slug = ?::citext; ",
                    forumMapper,
                    slug
            );
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public ForumDBModel getForumDBBySlug(String slug){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * " +
                            "FROM forums f " +
                            "WHERE slug = ?::citext; ",
                    forumDBMapper,
                    slug
            );
        } catch (Exception e){
            return null;
        }
    }

    public int getForumIdBySlug(String slug){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id " +
                            "FROM forums f " +
                            "WHERE slug = ?::citext; ",
                    Integer.class,
                    slug
            );
        } catch (Exception e){
            System.out.print(e);
            return -1;
        }
    }

   /* public List<UserModel> getUsersOfForum(int forumId, int limit, String since, boolean desc) {

        String query =
                "SELECT u.* " +
                "FROM (SELECT fu.user_id FROM forums_users fu WHERE fu.forum_id = ? GROUP BY fu.user_id ) lu " +
                "LEFT JOIN users u ON u.id = lu.user_id ";

        ArrayList params = new ArrayList() ;
        params.add(forumId);

        if (since!= null && !since.isEmpty()) {
            if (desc) {
                query += " WHERE u.nickname < ?::citext ";
            } else {
                query += " WHERE u.nickname > ?::citext ";
            }
            params.add(since);
        }

        if (desc) {
            query += " ORDER BY u.nickname DESC ";
        } else {
            query += " ORDER BY u.nickname ASC ";
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
    }*/

   /* public List<UserModel> getUsersOfForum(int forumId, int limit, String since, boolean desc) {

        String query =
                "SELECT * " +
                        "FROM users WHERE id IN " +
                        "(SELECT user_id FROM forums_users WHERE forum_id=?) ";
        ArrayList params = new ArrayList() ;
        params.add(forumId);

        if (since!= null && !since.isEmpty()) {
            if (desc) {
                query += " AND nickname < ?::citext ";
            } else {
                query += " AND nickname > ?::citext ";
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
                userMapper
        );
    }*/

    public List<UserModel> getUsersOfForum(int forumId, int limit, String since, boolean desc) {

        String query =
                "SELECT about, nickname, fullname, email " +
                        "FROM forums_users " +
                        "WHERE forum_id=? ";
        ArrayList params = new ArrayList() ;
        params.add(forumId);

        if (since!= null && !since.isEmpty()) {
            if (desc) {
                query += " AND nickname < ?::citext ";
            } else {
                query += " AND nickname > ?::citext ";
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
                userMapper
        );
    }


}
