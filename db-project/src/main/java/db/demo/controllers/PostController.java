package db.demo.controllers;


import db.demo.models.PostDBModel;
import db.demo.services.ForumService;
import db.demo.services.PostService;
import db.demo.services.ThreadService;
import db.demo.services.UserService;
import db.demo.views.MessageModel;
import db.demo.views.PostFullModel;
import db.demo.views.PostModel;
import db.demo.views.PostUpdateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/post")
@RestController
public class PostController {

    private PostService postService;
    private UserService userService;
    private ForumService forumService;
    private ThreadService threadService;

    @Autowired
    public PostController(
            PostService postService,
            UserService userService,
            ForumService forumService,
            ThreadService threadService
    ) {
        this.postService = postService;
        this.userService = userService;
        this.forumService = forumService;
        this.threadService = threadService;
    }

    @GetMapping(value = "/{id}/details")
    public ResponseEntity getPostDetails(
            @PathVariable(name = "id") int id,
            @RequestParam(name = "related", required = false) Set<String> requiredInfo
    ) {

        PostFullModel postFull = new PostFullModel();

        //long st = System.nanoTime();
        PostDBModel post = postService.getPostDBById(id);
        //System.out.println("getPostDBById:" + (System.nanoTime() - st));

        if (post == null) {
            MessageModel error = new MessageModel("Can't find post with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        postFull.setPost(new PostModel(post));

        if (requiredInfo != null && !requiredInfo.isEmpty()) {
            if (requiredInfo.contains("user")) {
               // st = System.nanoTime();
                postFull.setAuthor(userService.getUserById(post.getAuthor()));
                //System.out.println("getPostDBById:" + (System.nanoTime() - st));
            }
            if (requiredInfo.contains("forum")) {
                //st = System.nanoTime();
                postFull.setForum(forumService.getForumById(post.getForum()));
               // System.out.println("getForumById:" + (System.nanoTime() - st));
            }
            if (requiredInfo.contains("thread")) {
              //  st = System.nanoTime();
                postFull.setThread(threadService.getThreadById(post.getThread()));
              //  System.out.println("getThreadById:" + (System.nanoTime() - st));
            }
        }
        long end = System.nanoTime();
        return ResponseEntity.status(HttpStatus.OK).body(postFull);
    }

    @PostMapping(value = "/{id}/details")
    public ResponseEntity updatePostData(
            @PathVariable(name = "id") int id,
            @RequestBody PostUpdateModel newPostData
    ) {
        PostModel post = postService.getPostById(id);
        if (post == null) {
            MessageModel error = new MessageModel("Can't find post with id " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        if (newPostData.getMessage() != null && post.getMessage().compareTo(newPostData.getMessage()) != 0 ) {
            post.setMessage(newPostData.getMessage());
            postService.updatePostData(post.getId(), newPostData);
            post.setEdited(true);
        }
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
}
