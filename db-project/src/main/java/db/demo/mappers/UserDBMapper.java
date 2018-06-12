package db.demo.mappers;

import db.demo.models.UserDBModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDBMapper implements RowMapper<UserDBModel> {
    @Override
    public UserDBModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserDBModel userModel = new UserDBModel();

        userModel.setId(resultSet.getInt("id"));
        userModel.setAbout(resultSet.getString("about"));
        userModel.setEmail(resultSet.getString("email"));
        userModel.setFullname(resultSet.getString("fullname"));
        userModel.setNickname(resultSet.getString("nickname"));

        return userModel;
    }
}

