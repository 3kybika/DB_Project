package db.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.demo.views.VoteModel;

public class VoteDBModel {

    private Integer id;
    private Integer user_id;
    private String user_nickname;
    private Integer thread_id;
    private Integer voice;

    @JsonCreator
    public VoteDBModel(
            @JsonProperty("id") Integer id,
            @JsonProperty("user_id") Integer user_id,
            @JsonProperty("user_nickname") String user_nickname,
            @JsonProperty("thread_id") Integer thread_id,
            @JsonProperty("voice") Integer voice
    ) {
        this.id = id;
        this.user_id = user_id;
        this.user_nickname = user_nickname;
        this.thread_id = thread_id;
        this.voice = voice;
    }

    public VoteDBModel(VoteModel vote){
        this.user_nickname = vote.getNickname();
        this.thread_id = vote.getThread();
        this.voice = vote.getVoice();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThreadId() {
        return thread_id;
    }

    public void setThreadId(Integer thread_id) {
        this.thread_id = thread_id;
    }

    public Integer getUserId() {
        return user_id;
    }

    public void setUserId(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public String getUserNickname() {
        return user_nickname;
    }

    public void setUserNickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }
}
