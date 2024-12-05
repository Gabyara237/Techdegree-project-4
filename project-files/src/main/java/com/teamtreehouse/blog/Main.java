package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.*;

public class Main {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");

    public static void main(String[] args) {
        staticFileLocation("/public");



        BlogDao dao = new SimpleBlogDAO();

        get("/", (req,res) -> {
            Map<String,String> model = new HashMap<>();
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get ("/detail/:slug",(req, res) -> {
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry",dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/detail/:slug",(req,res) ->{

            BlogEntry blogEntry = dao.findEntryBySlug(req.queryParams("slug"));
            String author = req.queryParams("name");
            String newComment = req.queryParams("comment");
            String dateFormatted = dateFormat.format(new Date());

            Comment comment = new Comment(author,newComment,dateFormatted);

            blogEntry.addComment(comment);
            return null;
        });

        get("/new",(req,res) ->{
            Map<String,String> model = new HashMap<>();
            return new ModelAndView(model,"new.hbs");
        },new HandlebarsTemplateEngine());

        post("/new", (req,res) ->{
            dao.addEntry(createUpdateEntry(req));
            return null;
        },new HandlebarsTemplateEngine());

        get("/edit/:slug",(req,res) -> {
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry",dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"edit.hbs");
        },new HandlebarsTemplateEngine());

        post("/edit/:slug",(req,res)-> {
            dao.addEntry(createUpdateEntry(req));
            return null;
        },new HandlebarsTemplateEngine());

        get("/password",(req,res) ->{
            Map<String,String> model = new HashMap<>();
            return new ModelAndView(model,"password.hbs");
        },new HandlebarsTemplateEngine());

        post("/password", (req,res) ->{
            String password = req.queryParams("password");
            return null;
        });
    }

    public static BlogEntry createUpdateEntry(spark.Request req){
        String title = req.queryParams("title");
        String content = req.queryParams("entry");
        String dateFormatted = dateFormat.format(new Date());
        return new BlogEntry(title,content,dateFormatted);
    }

}
