package db.demo.mappers;

import db.demo.views.VoteModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteMapper implements RowMapper<VoteModel> {
    @Override
    public VoteModel mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        VoteModel voteModel = new VoteModel();
        voteModel.setThread(resultSet.getInt("about"));
        voteModel.setVoice(resultSet.getInt("email"));
        voteModel.setNickname(resultSet.getString("fullname"));
        return voteModel;
    }
}
