package db.demo.controllers;


import db.demo.models.ForumDBModel;
import db.demo.models.ThreadDBModel;
import db.demo.models.UserDBModel;
import db.demo.services.ForumService;
import db.demo.services.ThreadService;
import db.demo.services.UserService;
import db.demo.views.ForumModel;
import db.demo.views.MessageModel;
import db.demo.views.ThreadModel;
import db.demo.views.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RequestMapping("/api/forum")
@RestController
public class ForumController {

    private ForumService forumService;
    private UserService userService;
    private ThreadService threadService;

    @Autowired
    public ForumController(
            ForumService forumService,
            ThreadService threadService,
            UserService userService
    ) {
        this.forumService = forumService;
        this.threadService = threadService;
        this.userService = userService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity createForum(@RequestBody ForumModel forum) {

        UserDBModel userDB = userService.getUserDBByNickname(forum.getUser());

        if (userDB == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("Can't find user with nickname:" + forum.getUser()));
        }
        forum.setUser(userDB.getNickname());
        ForumDBModel forumDB = new ForumDBModel(forum);
        forumDB.setUserId(userDB.getId());
        forumDB.setUserNickame(userDB.getNickname());
        try {
            forumService.createForum(forumDB);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.getForumBySlug(forum.getSlug()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(forum);
    }


    @GetMapping(value = "{slug}/details")
    public ResponseEntity getForumDetails(@PathVariable(name = "slug") String slug) {
        long st = System.nanoTime();
        ForumModel forum = forumService.getForumBySlug(slug);
        System.out.println("getForumBySlug:" + (System.nanoTime() - st));

        if (forum == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("Can't find forum " + slug));
        }
        return ResponseEntity.status(HttpStatus.OK).body(forum);
    }

    @PostMapping(value = "/{slug}/create")
    public ResponseEntity createThread(
            @PathVariable(name = "slug") String slug,
            @RequestBody ThreadModel thread)
    {
        ForumDBModel forum = forumService.getForumDBBySlug(slug);
        if (forum == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("Can't find forum " + slug));
        }
        thread.setForum(forum.getSlug());

        UserDBModel user = userService.getUserDBByNickname(thread.getAuthor());
        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("Can't find user with id " + thread.getAuthor()));
        }
        thread.setAuthor(user.getNickname());


        ThreadDBModel threadDB = new ThreadDBModel(thread);
        if(threadDB.getCreated() == null){
            threadDB.setCreated(new Timestamp(System.currentTimeMillis()).toInstant().toString());
        }
        threadDB.setAuthorId(user.getId());
        threadDB.setAuthorNickname(user.getNickname());
        threadDB.setForum(forum.getId());
        threadDB.setForumSlug(forum.getSlug());

        try{
            thread.setId(threadService.createThread(threadDB));
            userService.addingForumUser(user, forum.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (DuplicateKeyException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadService.getThreadBySlug(thread.getSlug()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadService.getThreadBySlug(thread.getSlug()));
        }
    }

    @GetMapping(value = "{slug}/threads")
    public ResponseEntity getThreads(
            @PathVariable(name = "slug") String slug,
            @RequestParam(value = "limit", defaultValue = "0") int limit,
            @RequestParam(value = "since", defaultValue = "") String since,
            @RequestParam(value = "desc", defaultValue = "false") boolean desc
    ) {
        long st = System.nanoTime();
        int forumId = forumService.getForumIdBySlug(slug);
        System.out.println("getForumIdBySlug:" + (System.nanoTime() - st));

        if( forumId == -1){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("Can't find forum by slug: " + slug));
        }
        st = System.nanoTime();
        List<ThreadModel> result = threadService.getThreadsByForumId(forumId, limit, since, desc);
        System.out.println("getThreadsByForumId:" + (System.nanoTime() - st));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "{slug}/users")
    public ResponseEntity getUsers(
            @PathVariable(name = "slug") String slug,
            @RequestParam(value = "limit", defaultValue = "0") int limit,
            @RequestParam(value = "since", defaultValue = "") String since,
            @RequestParam(value = "desc", defaultValue = "false") boolean desc
    ) {
        long st = System.nanoTime();
        int forumId  = forumService.getForumIdBySlug(slug);
        System.out.println("getForumIdBySlug:" + (System.nanoTime() - st));

        if (forumId == -1) {
            MessageModel error = new MessageModel("Can't find forum " + slug);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        st = System.nanoTime();
        List<UserModel> users = forumService.getUsersOfForum(forumId, limit, since, desc);
        System.out.println("getUsersOfForum:" + (System.nanoTime() - st));

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

}
