package db.demo.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusModel {

    private int forum;
    private int post;
    private int thread;
    private int user;

    @JsonCreator
    public StatusModel(
            @JsonProperty("forum") int forum,
            @JsonProperty("post") int post,
            @JsonProperty("thread") int thread,
            @JsonProperty("user") int user
    ) {
        this.forum = forum;
        this.post = post;
        this.thread = thread;
        this.user = user;
    }

    public StatusModel() {
    }

    public int getForum() {
        return forum;
    }

    public void setForum(int forum) {
        this.forum = forum;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
