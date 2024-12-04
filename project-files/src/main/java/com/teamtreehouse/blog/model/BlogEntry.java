package com.teamtreehouse.blog.model;

import java.util.Date;
import java.util.List;

public class BlogEntry {

    private String title;
    private String content;
    private Date date;
    private List<Comment> comments ;


    public BlogEntry(  String title, String content,Date date) {
        this.date = date;
        this.content = content;
        this.title = title;
    }

    // ######################## GETTERS #######################
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public List<Comment> getComments() {
        return comments;
    }

    // ######################## SETTERS #######################

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    public boolean addComment(Comment comment) {
        // Store these comments!
        comments.add(comment);
        return false;
    }
}
