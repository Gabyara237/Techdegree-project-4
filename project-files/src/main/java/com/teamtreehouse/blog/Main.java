package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.thedeanda.lorem.Lorem;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.*;

import com.thedeanda.lorem.LoremIpsum;

import static spark.Spark.*;

public class Main {
    //Standard format for displaying dates in entries and comments.
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
    private static final String FLASH_MESSAGE_KEY = "flash_message";

    private static  BlogDao dao = new SimpleBlogDAO();

    public static void main(String[] args) {
        staticFileLocation("/public");
        // DAO instance created to manage blog entries
        createThreeEntries();
        // Checks if the password cookie exists and assigns it as attribute
        before((req,res)->{
            if(req.cookie("password")!=null){
                req.attribute("password",req.cookie("password"));

            }
        });

        // Ensure that the user is authenticated when trying to enter the path `/new`.
        before("/new", (req,res) ->{
            if (req.attribute("password") == null || !req.attribute("password").equals("admin")){
                setFlashMessage(req,"Whoops, please enter your administrator password!");
                res.redirect("/password?redirect=new");
                halt();
            }
        });

        // Main Route showing all blog entries.
        get("/", (req,res) -> {
            Map<String,Object> model = new HashMap<>();
            model.put("entries",dao.findAllEntries());
            String flashMessage = captureFlashMessage(req);
            if(flashMessage != null){
                model.put("flashMessage", flashMessage);
            }
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        // Route to display a specific entry according to its slug.
        get ("/detail/:slug",(req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry",dao.findEntryBySlug(req.params("slug")));
            String flashMessage = captureFlashMessage(req);
            if(flashMessage != null){
                model.put("flashMessage", flashMessage);
            }
            return new ModelAndView(model,"detail.hbs");
        }, new HandlebarsTemplateEngine());

        // Route to add a comment to a specific entry.
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

        // Route that displays the form to create a new entry.
        get("/new",(req,res) ->{
            if(req.attribute("password") == null || !req.attribute("password").equals("admin")){
                res.redirect("/password?redirect=new");
                halt();
            }
            Map<String,String> model = new HashMap<>();

            return new ModelAndView(model,"new.hbs");
        },new HandlebarsTemplateEngine());

        // Route to process the creation of a new entry.
        post("/new", (req,res) ->{
            dao.addEntry(createUpdateEntry(req));
            setFlashMessage(req,"Post entry successfully created!");
            res.redirect("/");
            return null;
        });

        // Route to show the edit form of an existing entry.
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

        // Route to process the editing of an existing entry.
        post("/edit/:slug",(req,res)-> {
            String slug = req.params("slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            blogEntry.setTitle(req.queryParams("title"));
            blogEntry.setContent(req.queryParams("entry"));
            String tagsString = req.queryParams("tags");
            List<String> tags = new ArrayList<>();
            if(tagsString !=null && !tagsString.isEmpty()){
                tags= Arrays.asList(tagsString.split("\\s*,\\s*"));
            }
            blogEntry.setTags(tags);
            setFlashMessage(req,"Post entry successfully edited!");
            res.redirect("/detail/"+slug);
            return null;
        });

        // Route to delete an entry.
        post("/delete/:slug",(req,res)->{
            if(req.attribute("password") ==null || !req.attribute("password").equals("admin")){
                String slug = req.params("slug");
                res.redirect("/password?slug="+ slug);
                halt();
            }
            String slug = req.params("slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            dao.deleteEntry(blogEntry);
            setFlashMessage(req,"Post entry successfully deleted!");
            res.redirect("/");
            return  null;

        });

        // Route to display the password authentication form.
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
            String flashMessage = captureFlashMessage(req);
            if (flashMessage != null){
                model.put("flashMessage",flashMessage);
            }
            return new ModelAndView(model,"password.hbs");
        },new HandlebarsTemplateEngine());

        // Route to process the authentication form
        post("/password", (req,res) ->{
            String password = req.queryParams("password");
            String slug = req.queryParams("slug");
            res.cookie("password",password);
            if (password==null || !password.equals("admin")) {
                setFlashMessage(req,"Whoops, wrong password, try again!!");
                res.redirect("/password");
                halt();
            }

            if(slug != null){
                res.redirect("/edit/"+slug);
            }else{
                res.redirect("/new");
            }
            return null;
        });
    }

    // Method to create or update an entry from the form data.
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

    // Set a flash message in the session
    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    // Gets a flash message without deleting it from the session.
    private static String getFlashMessage(Request req){
        if (req.session(false) == null){
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)){
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    // Captures and removes a flash message from the session.
    private static String captureFlashMessage(Request req){
        String message = getFlashMessage(req);
        if (message != null){
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

    private static void createThreeEntries(){
        List<String> listTitle = new ArrayList<>();
        List<BlogEntry> listBlogEntries = new ArrayList<>();
        Lorem lorem = LoremIpsum.getInstance();

        listTitle.add("Why choose java as a programming language?");
        listTitle.add("10 tips to improve your Java code");
        listTitle.add("Top Java Libraries and Frameworks for 2025");

        for(int i=0; i<3; i++){
            String title = listTitle.get(i);
            String content = lorem.getParagraphs(2, 4);
            String dateFormatted = dateFormat.format(new Date());
            String tagsString ="Java, Programming,code";
            List<String> tags = new ArrayList<>();

            tags= Arrays.asList(tagsString.split("\\s*,\\s*"));

            BlogEntry blogEntry= new BlogEntry(title,content,dateFormatted,tags);
            Comment comment = new Comment("Gaby", lorem.getParagraphs(1,2),dateFormatted );
            blogEntry.addComment(comment);
            dao.addEntry(blogEntry);

        }


    }


}
