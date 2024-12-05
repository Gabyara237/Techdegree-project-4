package com.teamtreehouse.blog.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BlogEntry {

    private String title;
    private String content;
    private Date date;
    private String author;
    private List<Comment> comments;
    private String slug;


    public BlogEntry( String title, String author, String content,Date date) {
        this.date = date;
        this.content = content;
        this.title = title;
        this.author = author;
    }

    // ######################## GETTERS #######################

    public String getSlug() {
        return slug;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;
        return Objects.equals(title, blogEntry.title) && Objects.equals(content, blogEntry.content) && Objects.equals(date, blogEntry.date) && Objects.equals(comments, blogEntry.comments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(date);
        result = 31 * result + Objects.hashCode(comments);
        return result;
    }

    public boolean addComment(Comment comment) {
        // Store these comments!
        comments.add(comment);
        return false;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
