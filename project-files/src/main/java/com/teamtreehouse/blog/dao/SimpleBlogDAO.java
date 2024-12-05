package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlogDAO implements BlogDao {

    private List<BlogEntry>  blogEntries;

    public SimpleBlogDAO() { blogEntries = new ArrayList<>();}

   @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return blogEntries.add(blogEntry);
    }

    @Override
    public List<BlogEntry> findAllEntries(){
        return new ArrayList<>(blogEntries);
    }

    @Override
    public BlogEntry findEntryBySlug(String slug){
        return blogEntries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

}
