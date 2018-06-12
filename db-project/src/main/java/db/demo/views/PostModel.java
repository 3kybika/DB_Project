package db.demo.views;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.demo.models.PostDBModel;

public class PostModel {

    private String author;
    private String created;
    private String forum;
    private int id;
    private boolean isEdited;
    private String message;
    private int parent;
    private int thread;

    @JsonCreator
    public PostModel(
            @JsonProperty("author") String author,
            @JsonProperty("created") String created,
            @JsonProperty("forum") String forum,
            @JsonProperty("id") int id,
            @JsonProperty("isEdited") boolean isEdited,
            @JsonProperty("message") String message,
            @JsonProperty("parent") int parent,
            @JsonProperty("thread") int thread
    ) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
    }

    public PostModel() {
    }

    public PostModel(PostDBModel post) {
        this.author = post.getAuthorNickname();
        this.created = post.getCreated();
        this.forum = post.getForumSlug();
        this.id = post.getId();
        this.isEdited = post.getIsEdited();
        this.message = post.getMessage();
        this.parent = post.getParent();
        this.thread = post.getThread();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
