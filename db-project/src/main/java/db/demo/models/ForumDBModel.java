package db.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.demo.views.ForumModel;

public class ForumDBModel {
    private int     id;
    private int posts;
    private int threads;
    private String  slug;
    private String  title;
    private String  author_nickname;
    private int     author_id;

    @JsonCreator
    public ForumDBModel(
            @JsonProperty("id")      int    id,
            @JsonProperty("posts")   int    posts,
            @JsonProperty("threads") int    threads,
            @JsonProperty("slug")    String slug,
            @JsonProperty("title")   String title,
            @JsonProperty("author_id") int    user_id
    ) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.author_id = user_id;
    }

    public ForumDBModel(){
    }

    public ForumDBModel(ForumModel forum){
        this.slug = forum.getSlug();
        this.title = forum.getTitle();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUserId() {
        return author_id;
    }

    public void setUserId(Integer user_id) {
        this.author_id = user_id;
    }

    public String getUserNickame() {
        return author_nickname;
    }

    public void setUserNickame(String author_nickname) {
        this.author_nickname = author_nickname;
    }
}
