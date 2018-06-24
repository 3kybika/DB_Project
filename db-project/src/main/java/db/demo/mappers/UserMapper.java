package db.demo.mappers;

import db.demo.views.UserModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<UserModel> {
    @Override
    public UserModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserModel userModel = new UserModel();
        userModel.setAbout(resultSet.getString("about"));
        userModel.setEmail(resultSet.getString("email"));
        userModel.setFullname(resultSet.getString("fullname"));
        userModel.setNickname(resultSet.getString("nickname"));
        return userModel;
    }
}