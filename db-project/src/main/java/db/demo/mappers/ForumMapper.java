package db.demo.mappers;

import db.demo.views.ForumModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<ForumModel> {
    @Override
    public ForumModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ForumModel forumModel = new ForumModel(
                resultSet.getInt("posts"),
                resultSet.getString("slug"),
                resultSet.getInt("threads"),
                resultSet.getString("title"),
                resultSet.getString("author_nickname")
        );
        return forumModel;
    }
}

