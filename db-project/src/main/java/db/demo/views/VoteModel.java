package db.demo.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VoteModel {

    private String nickname;
    private int voice;
    private int thread;

    @JsonCreator
    public VoteModel(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") int voice
    ) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public VoteModel() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}

