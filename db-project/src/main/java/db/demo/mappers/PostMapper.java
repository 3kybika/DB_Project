package db.demo.mappers;

import db.demo.utils.TimestampUtil;
import db.demo.views.PostModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<PostModel> {
    @Override
    public PostModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        PostModel postModel = new PostModel();

        postModel.setId(        resultSet.getInt(       "id"));
        postModel.setAuthor(    resultSet.getString(    "author_nickname"));
        postModel.setCreated(   resultSet.getTimestamp( "created").toInstant().toString());
        postModel.setForum(     resultSet.getString(    "forum_slug"));
        postModel.setEdited(    resultSet.getBoolean(   "isEdited"));
        postModel.setMessage(   resultSet.getString(    "message"));
        postModel.setParent(    resultSet.getInt(       "parent"));
        postModel.setThread(    resultSet.getInt(       "thread_id"));
        return postModel;
    }
}
