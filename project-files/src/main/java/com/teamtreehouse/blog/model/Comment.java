package com.teamtreehouse.blog.model;

import java.util.Date;

public class Comment {
    private String author;
    private String content;
    private String date;

    // ######################## CONSTRUCTOR #######################
    public Comment(String author, String content, String date) {
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

    public String getDate() {
        return date;
    }




}

