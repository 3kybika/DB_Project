package db.demo.mappers;

import db.demo.models.ForumDBModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumDBMapper implements RowMapper<ForumDBModel> {
    @Override
    public ForumDBModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ForumDBModel forumModel = new ForumDBModel();

        forumModel.setId(resultSet.getInt("id"));
        forumModel.setSlug(resultSet.getString("slug"));
        forumModel.setTitle(resultSet.getString("title"));
        forumModel.setUserId(resultSet.getInt("author_id"));
        forumModel.setUserNickame(resultSet.getString("author_nickname"));
        forumModel.setThreads(resultSet.getInt("threads"));
        forumModel.setPosts(resultSet.getInt("posts"));

        return forumModel;
    }
}