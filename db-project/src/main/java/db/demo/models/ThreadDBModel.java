package db.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.demo.views.ThreadModel;

public class ThreadDBModel {
    private int id;
    private int author_id;
    private String author_nickname;
    private int votes;
    private String created;
    private int forum_id;
    private String forum_slug;
    private String message;
    private String slug;
    private String title;

    @JsonCreator
    public ThreadDBModel(
            @JsonProperty("id") int id,
            @JsonProperty("auhtor_id") int author_id,
            @JsonProperty("votes") int votes,
            @JsonProperty("created") String created,
            @JsonProperty("forum_id") int forum_id,
            @JsonProperty("message") String message,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title
    ) {
        this.id = id;
        this.author_id = author_id;
        this.created = created;
        this.forum_id = forum_id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public ThreadDBModel() {
    }
    public ThreadDBModel(ThreadModel thread) {
        this.message = thread.getMessage();
        this.title = thread.getTitle();
        this.slug = thread.getSlug();
        this.created = thread.getCreated();
        this.votes = thread.getVotes();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getAuthorId() {
        return author_id;
    }

    public void setAuthorId(int author_id) {
        this.author_id = author_id;
    }

    public String getAuthorNick() {
        return author_nickname;
    }

    public void setAuthorNickname(String author_nickname) {
        this.author_nickname = author_nickname;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getForum() {
        return forum_id;
    }

    public void setForum(int forum_id) {
        this.forum_id = forum_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getForumSlug() {
        return forum_slug;
    }

    public void setForumSlug(String slug) {
        this.forum_slug = slug;
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
}

