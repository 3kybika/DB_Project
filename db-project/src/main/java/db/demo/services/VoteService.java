package db.demo.services;

import db.demo.models.VoteDBModel;
import db.demo.views.VoteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public VoteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear(){
        jdbcTemplate.update("TRUNCATE TABLE votes CASCADE ;");
    }

    public Integer createVoice(VoteDBModel vote) {

        try {
            //наверное тут стоит тоже триггер завести...
             this.jdbcTemplate.update(
                     "INSERT INTO votes(user_id, user_nickname, voice, thread_id)" +
                     "VALUES (?, ?::citext, ?, ?);",
                     vote.getUserId(),
                     vote.getUserNickname(),
                     vote.getVoice(),
                     vote.getThreadId()
             );
            return this.jdbcTemplate.queryForObject(

                    "UPDATE threads SET votes=votes+? WHERE id=? RETURNING votes; ",
                    Integer.class,

                    vote.getVoice(),
                    vote.getThreadId()
            );
        } catch (DuplicateKeyException ex) {
            //Значит голос существует!
            int oldVoice = this.jdbcTemplate.queryForObject(
                    "SELECT voice " +
                            "FROM votes " +
                            "WHERE user_id = ? AND thread_id = ?;",
                    Integer.class,
                     vote.getUserId(),
                     vote.getThreadId()
            );

            if(oldVoice != vote.getVoice()) {
                this.jdbcTemplate.update(
                        "UPDATE votes " +
                                "SET voice = ? " +
                                "WHERE user_id = ? AND thread_id = ?; ",
                        vote.getVoice(),
                        vote.getUserId(),
                        vote.getThreadId()
                );

                String sql;
                if (vote.getVoice() > 0) {
                    sql = "UPDATE threads SET votes=votes+2 WHERE id=? RETURNING votes;";
                } else {
                    sql = "UPDATE threads SET votes=votes-2 WHERE id=? RETURNING votes;";
                }

                return this.jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        vote.getThreadId()
                );
            }
        }
        return this.jdbcTemplate.queryForObject(
                "SELECT votes " +
                        "FROM threads " +
                        "WHERE id = ?;",
                Integer.class,
                vote.getThreadId()
        );
    }

}
