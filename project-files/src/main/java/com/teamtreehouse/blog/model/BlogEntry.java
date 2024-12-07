package com.teamtreehouse.blog.model;

import java.io.IOException;
import java.util.*;

import com.github.slugify.Slugify;

public class BlogEntry {

    private String title;
    private String content;
    private String date;
   // private String author;
    private List<Comment> comments;
    private String slug;
    private List<String> tags;


    public BlogEntry( String title, String content, String date, List<String> tags) {
        this.date = date;
        this.content = content;
        this.title = title;
        this.comments = new ArrayList<>();
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();

       // this.author = author;
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public List<Comment> getComments() {
        return comments;
    }

    public String getDate() {
        return date;
    }

    public List<String> getTags() {
        return tags;
    }

// ######################## SETTERS #######################

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
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
        return comments.add(comment);

    }

}
