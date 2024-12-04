package com.teamtreehouse.blog.model;

import java.util.Date;

public class Comment {
    private String author;
    private String content;
    private Date date;

    // ######################## CONSTRUCTOR #######################
    public Comment(String author, String content, Date date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }

    // ######################## GETTERS #######################
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }




}

