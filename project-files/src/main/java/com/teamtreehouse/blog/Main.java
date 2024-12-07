package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.*;

import static spark.Spark.*;

public class Main {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");

    public static void main(String[] args) {
        staticFileLocation("/public");
        BlogDao dao = new SimpleBlogDAO();

        before((req,res)->{
            if(req.cookie("password")!=null){
                req.attribute("password",req.cookie("password"));

            }
        });

        get("/", (req,res) -> {
            Map<String,List<BlogEntry>> model = new HashMap<>();
            model.put("entries",dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get ("/detail/:slug",(req, res) -> {
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry",dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/detail/:slug",(req,res) ->{

            String slug = req.params("slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            String author = req.queryParams("name");
            String newComment = req.queryParams("comment");
            String dateFormatted = dateFormat.format(new Date());

            Comment comment = new Comment(author,newComment,dateFormatted);
            blogEntry.addComment(comment);
            res.redirect("/detail/"+slug);
            return null;
        });

        get("/new",(req,res) ->{
            if(req.attribute("password") == null || !req.attribute("password").equals("admin")){
                res.redirect("/password?redirect=new");
                halt();
            }
            Map<String,String> model = new HashMap<>();
            return new ModelAndView(model,"new.hbs");
        },new HandlebarsTemplateEngine());

        post("/new", (req,res) ->{
            dao.addEntry(createUpdateEntry(req));
            res.redirect("/");
            return null;
        });

        get("/edit/:slug",(req,res) -> {
            if(req.attribute("password") == null || !req.attribute("password").equals("admin")){
                String slug = req.params("slug");
                res.redirect("/password?slug="+ slug);
                halt();
            }
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry",dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"edit.hbs");
        },new HandlebarsTemplateEngine());

        post("/edit/:slug",(req,res)-> {
            String slug = req.params("slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            blogEntry.setTitle(req.queryParams("title"));
            blogEntry.setContent(req.queryParams("entry"));
            res.redirect("/detail/"+slug);
            return null;
        });

        post("/delete/:slug",(req,res)->{
            if(req.attribute("password") ==null || !req.attribute("password").equals("admin")){
                String slug = req.params("slug");
                res.redirect("/password?slug="+ slug);
                halt();
            }
            String slug = req.params("slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            dao.deleteEntry(blogEntry);
            res.redirect("/");
            return  null;

        });
        get("/password",(req,res) ->{
            Map<String,String> model = new HashMap<>();
            String slug = req.queryParams("slug");
            String redirectTo= req.queryParams("redirect");
            if (slug !=null){
                model.put("slug",slug);
            }
            if (redirectTo !=null){
                model.put("redirectTo",redirectTo);
            }
            return new ModelAndView(model,"password.hbs");
        },new HandlebarsTemplateEngine());

        post("/password", (req,res) ->{
            String password = req.queryParams("password");
            String slug = req.queryParams("slug");
            res.cookie("password",password);

            if(slug != null){
                res.redirect("/edit/"+slug);
            }else{
                res.redirect("/new");
            }
            return null;
        });
    }

    public static BlogEntry createUpdateEntry(spark.Request req){
        String title = req.queryParams("title");
        String content = req.queryParams("entry");
        String dateFormatted = dateFormat.format(new Date());
        String tagsString =req.queryParams("tags");

        List<String> tags = new ArrayList<>();
        if(tagsString !=null && !tagsString.isEmpty()){
            tags= Arrays.asList(tagsString.split("\\s*,\\s*"));
        }
        return new BlogEntry(title,content,dateFormatted,tags);
    }

}
