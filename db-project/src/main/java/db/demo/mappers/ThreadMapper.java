package db.demo.mappers;

import db.demo.utils.TimestampUtil;
import db.demo.views.ThreadModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ThreadMapper implements RowMapper<ThreadModel> {
    @Override
    public ThreadModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ThreadModel threadModel = new ThreadModel();

        threadModel.setAuthor(resultSet.getString("author_nickname"));
        threadModel.setForum(resultSet.getString("forum_slug"));
        threadModel.setId(resultSet.getInt("id"));
        threadModel.setMessage(resultSet.getString("message"));
        threadModel.setSlug(resultSet.getString("slug"));
        threadModel.setTitle(resultSet.getString("title"));
        threadModel.setVotes(resultSet.getInt("votes"));
        threadModel.setCreated(resultSet.getTimestamp("created").toInstant().toString());


        return threadModel;
    }
}