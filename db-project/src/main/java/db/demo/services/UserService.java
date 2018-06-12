package db.demo.services;

import db.demo.mappers.UserDBMapper;
import db.demo.mappers.UserMapper;
import db.demo.models.PostDBModel;
import db.demo.models.UserDBModel;
import db.demo.views.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private JdbcTemplate jdbcTemplate;
    private UserMapper userMapper = new UserMapper();
    private UserDBMapper userDBMapper = new UserDBMapper();

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear(){
        jdbcTemplate.update("TRUNCATE TABLE users CASCADE ;");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

    public void createUser(UserModel user) {
        jdbcTemplate.update(
                "INSERT INTO users (about, email, fullname, nickname) " +
                        "VALUES (?, ?, ?, ?)",
                user.getAbout(),
                user.getEmail(),
                user.getFullname(),
                user.getNickname()
        );
    }


    public List<UserModel> getDublicateUsers(UserModel user) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM users " +
                            "WHERE nickname=?::citext OR email=?::citext;",
                    userMapper,
                    user.getNickname(),
                    user.getEmail()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public UserModel getUserByNickname(String nickname) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE lower(nickname) = lower(?);",
                    userMapper,
                    nickname
            );
        } catch (Exception e) {
            return null;
        }
    }

    public UserDBModel getUserDBByNickname(String nickname) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users " +
                            "WHERE nickname=?::citext",
                    userDBMapper,
                    nickname
            );
        } catch (Exception e) {
            return null;
        }
    }

    public UserModel getUserById(int id){
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users " +
                            "WHERE id=?;",
                    userMapper,
                    id
            );
        } catch (Exception e) {
            return null;
        }
    }

    public UserModel updateUserInfo(UserModel user) {
        StringBuilder querry = new StringBuilder();
        querry.append("UPDATE users SET ");

        List<String> updatedValues = new ArrayList<String>();
        if (user.getAbout() != null) {
            updatedValues.add("about = '" + user.getAbout() + "'");
        }
        if (user.getEmail() != null) {
            updatedValues.add("email = '" + user.getEmail() + "'");
        }
        if (user.getFullname() != null) {
            updatedValues.add("fullname = '" + user.getFullname() + "'");
        }

        querry.append(
                String.join(", ", updatedValues)
                        + " WHERE nickname = '" + user.getNickname() + "' "
        );
        querry.append(" RETURNING about, fullname, email, nickname;");
        if (!updatedValues.isEmpty()) {
            return jdbcTemplate.queryForObject(querry.toString(), userMapper);
        }
        return getUserByNickname(user.getNickname());
    }

    public void addingForumUsers(List<Integer> ids, int forumId)  throws SQLException {
       /* String query = "INSERT INTO forums_users(user_id, forum_id) VALUES (?, ?) ON CONFLICT (forum_id, user_id) DO NOTHING; ";
        try (
                Connection con = this.jdbcTemplate.getDataSource().getConnection();
                PreparedStatement ps = con.prepareStatement(query, Statement.NO_GENERATED_KEYS)
        ) {
            for (Integer id : ids) {
                ps.setInt(1, id);
                ps.setInt(2, forumId);

                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e){
            System.out.print(e);
        }*/
        for (Integer id : ids) {
            try {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO forums_users(user_id, forum_id) VALUES (?, ?) ON CONFLICT (forum_id, user_id) DO NOTHING; ",
                            PreparedStatement.NO_GENERATED_KEYS
                    );
                    ps.setInt(1, id);
                    ps.setInt(2, forumId);
                    return ps;
                });
            } catch (Exception ex) {

            }
        }
    }
}
