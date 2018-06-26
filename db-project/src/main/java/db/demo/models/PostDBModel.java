package db.demo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import db.demo.views.PostModel;
import db.demo.views.PostModel;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PostDBModel {

    private int     author_id;
    private String  author_nickname;
    private String  created;
    private int     forum_id;
    private String  forum_slug;
    private int     id;
    private boolean isEdited;
    private String  message;
    private int     parent;
    private int     thread_id;
    private Object [] path;
    private int     root_post;

    @JsonCreator
    public PostDBModel(
            @JsonProperty("author_id")  int     author_id,
            @JsonProperty("created")    String  created,
            @JsonProperty("forum_id")   int     forum_id,
            @JsonProperty("id")         int     id,
            @JsonProperty("isEdited")   boolean isEdited,
            @JsonProperty("message")    String  message,
            @JsonProperty("parent")     int     parent,
            @JsonProperty("thread_id")  int     thread_id,
            @JsonProperty("root_post")  Object []    path,
            @JsonProperty("path")       int     root_post
    ) {
        this.author_id = author_id;
        this.created = created;
        this.forum_id = forum_id;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread_id = thread_id;
        this.path = path;
        this.root_post = root_post;
    }

    public PostDBModel() {
    }

    public PostDBModel(PostModel post) {
        this.id = post.getId();
        this.created = post.getCreated();
        this.isEdited = post.getIsEdited();
        this.message = post.getMessage();
        this.parent = post.getParent();
        this.thread_id = post.getThread();
        this.forum_slug = post.getForum();
    }

    public String getForumSlug(){
        return forum_slug;
    }

    public void setForumSlug(String forum_slug){
        this.forum_slug = forum_slug;
    }

    public int getAuthor() {
        return author_id;
    }

    public void setAuthor(int author_id) {
        this.author_id = author_id;
    }

    public String getAuthorNickname() {
        return  author_nickname;
    }

    public void setAuthorNickname(String nickname){ author_nickname = nickname; }

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

    public void setPath(  Object [] path) {
        this.path = path;
    }

    public Object [] getPath() {
        return path;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getThread() {
        return thread_id;
    }

    public void setThread(int thread_id) {
        this.thread_id = thread_id;
    }

    public int getRootPost() {
        return root_post;
    }

    public void setRootPost(int root_post) {
        this.root_post = root_post;
    }
}
