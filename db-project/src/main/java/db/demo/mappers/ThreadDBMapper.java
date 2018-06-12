package db.demo.mappers;

import db.demo.models.ThreadDBModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadDBMapper implements RowMapper<ThreadDBModel> {
    @Override
    public ThreadDBModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ThreadDBModel threadModel = new ThreadDBModel();

        threadModel.setId(              resultSet.getInt(       "id"));
        threadModel.setAuthorId(        resultSet.getInt(       "author_id"));
        threadModel.setAuthorNickname(  resultSet.getString(    "author_nickname"));
        threadModel.setCreated(         resultSet.getTimestamp( "created").toInstant().toString());
        threadModel.setForum(           resultSet.getInt(       "forum_id"));
        threadModel.setForumSlug(       resultSet.getString(    "forum_slug"));
        threadModel.setMessage(         resultSet.getString(    "message"));
        threadModel.setSlug(            resultSet.getString(    "slug"));
        threadModel.setTitle(           resultSet.getString(    "title"));
        threadModel.setVotes(           resultSet.getInt(       "votes"));
        return threadModel;
    }
}
