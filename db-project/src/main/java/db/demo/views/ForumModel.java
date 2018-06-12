package db.demo.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ForumModel {

    private int posts;
    private String slug;
    private int threads;
    private String title;
    private String user;

    @JsonCreator
    public ForumModel(
            @JsonProperty("posts") int posts,
            @JsonProperty("slug") String slug,
            @JsonProperty("threads") int threads,
            @JsonProperty("title") String title,
            @JsonProperty("user") String user
    ) {
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
    }


    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
