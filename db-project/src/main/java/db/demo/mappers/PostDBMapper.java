package db.demo.mappers;

import db.demo.models.PostDBModel;
import db.demo.utils.TimestampUtil;
import db.demo.views.PostModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDBMapper implements RowMapper<PostDBModel> {
    @Override
    public PostDBModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        PostDBModel postModel = new PostDBModel();

        postModel.setId(            resultSet.getInt(       "id"));
        postModel.setAuthor(        resultSet.getInt(       "author_id"));
        postModel.setAuthorNickname(resultSet.getString(    "author_nickname"));
        postModel.setCreated(       resultSet.getTimestamp("created").toInstant().toString());
        postModel.setForum(         resultSet.getInt(       "forum_id"));
        postModel.setForumSlug(     resultSet.getString(    "forum_slug"));
        postModel.setEdited(        resultSet.getBoolean(   "isEdited"));
        postModel.setMessage(       resultSet.getString(    "message"));
        postModel.setParent(        resultSet.getInt(       "parent"));
        postModel.setThread(        resultSet.getInt(       "thread_id"));
        postModel.setRootPost(      resultSet.getInt(       "root_post"));
        postModel.setPath(          (Object[])resultSet.getArray("path").getArray());

        Array arrayList = resultSet.getArray("path");
        Object[] path = new Object[]{};
        if (arrayList != null) {
            path = (Object[]) arrayList.getArray();
        }

        postModel.setPath(path);

        return postModel;
     }
}