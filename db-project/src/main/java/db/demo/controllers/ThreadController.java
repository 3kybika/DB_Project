package db.demo.controllers;

import db.demo.models.PostDBModel;
import db.demo.models.ThreadDBModel;
import db.demo.models.UserDBModel;
import db.demo.models.VoteDBModel;
import db.demo.services.ForumService;
import db.demo.services.PostService;
import db.demo.services.ThreadService;
import db.demo.services.UserService;
import db.demo.services.VoteService;
import db.demo.utils.TimestampUtil;
import db.demo.views.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RequestMapping("/api/thread")
@RestController
public class ThreadController {

    private ForumService forumService;
    private ThreadService threadService;
    private PostService postService;
    private UserService userService;
    private VoteService voteService;

    @Autowired
    public ThreadController(
            ForumService forumService,
            ThreadService threadService,
            PostService postService,
            UserService userService,

            VoteService voteService
    ) {
        this.forumService = forumService;
        this.threadService = threadService;
        this.postService = postService;
        this.userService = userService;
        this.voteService = voteService;
    }

    @PostMapping(path = "/{slug_or_id}/create")
    public ResponseEntity createPosts(
            @RequestBody List<PostModel> posts,
            @PathVariable(name = "slug_or_id") String slug_or_id
    ) {

        ThreadDBModel threadDB =  threadService.getThreadDBBySlugOrId(slug_or_id);

        if (threadDB == null) {
           MessageModel error = new MessageModel("Can't find thread " + slug_or_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(posts);
        }


        int forumId = threadDB.getForum();
        int threadId = threadDB.getId();
        String createdNow = new Timestamp(System.currentTimeMillis()).toInstant().toString();
        List<PostDBModel> postsForCreation = new ArrayList<>();
        Set<UserDBModel> usersForUpdating=new LinkedHashSet<>();
        //List<UserDBModel> usersForUpdating = new ArrayList<>();
        for(PostModel post : posts){

            post.setForum(threadDB.getForumSlug());
            post.setThread(threadId);
            if (post.getCreated() == null){
                post.setCreated(createdNow);
            }

            PostDBModel postForCreation = new PostDBModel(post);

            if (post.getParent() != 0) {
                PostDBModel parentPost = postService.getPostDBById(post.getParent());
                if (parentPost == null) {
                    MessageModel error = new MessageModel("Can't find parent with id: " + post.getParent());
                    System.out.print("Can't find parent with id: " + post.getParent());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
                if (parentPost.getThread() != threadDB.getId()) {
                    MessageModel error = new MessageModel("Parent post was created in another thread");
                    System.out.print("Parent post was created in another thread!");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                }
                Object[] path = parentPost.getPath();
                postForCreation.setPath(path);

                ArrayList arr = new ArrayList<Object>(Arrays.asList(path));
                postForCreation.setRootPost((Integer) arr.get(0));
            }


            UserDBModel author = userService.getUserDBByNickname(post.getAuthor());
            if (author == null) {
                MessageModel error = new MessageModel("Can't find user with nickname: " + post.getAuthor());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            usersForUpdating.add(author);

            postForCreation.setAuthor(author.getId());
            postForCreation.setAuthorNickname(author.getNickname());

            postForCreation.setThread(threadId);
            postForCreation.setForumSlug(threadDB.getForumSlug());
            postForCreation.setForum(forumId);
            postsForCreation.add(postForCreation);
        }
        try {
            postsForCreation = postService.createPostInThread(postsForCreation, forumId, threadId);
        } catch (Exception e) {
            MessageModel error = new MessageModel("Parent post was created in another thread");
            System.out.print(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        try {
            userService.addingForumUsers(usersForUpdating , forumId);
        } catch (Exception e) {
            MessageModel error = new MessageModel("Could not open database connection!");
            System.out.print(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        String slug = postsForCreation.get(0).getForumSlug();
        for(int i=0; i < postsForCreation.size(); i++ ){
            posts.get(i).setId(postsForCreation.get(i).getId());
            posts.get(i).setForum(slug);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(posts);
    }

    @PostMapping(path = "/{slug_or_id}/vote")
    public ResponseEntity createVote(
            @RequestBody VoteModel vote,
            @PathVariable(name = "slug_or_id") String slug_or_id
    ) {
        if (vote == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(vote);
        }

        UserDBModel userDB = userService.getUserDBByNickname(vote.getNickname());
        if (userDB == null) {
            MessageModel error = new MessageModel("Can't find user " + vote.getNickname());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        ThreadDBModel threadDB =  threadService.    getThreadDBBySlugOrId(slug_or_id);

        if (threadDB == null) {
            MessageModel error = new MessageModel("Can't find thread " + slug_or_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        VoteDBModel voteDB = new VoteDBModel(vote);
        voteDB.setThreadId(threadDB.getId());
        voteDB.setUserId(userDB.getId());

        Integer newVotes = voteService.createVoice(voteDB);

        voteDB.setUserId(userDB.getId());
        voteDB.setThreadId(threadDB.getId());

        ThreadModel thread = new ThreadModel(threadDB);
        thread.setVotes(newVotes);
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @GetMapping(value = "/{slug_or_id}/details")
    public ResponseEntity getThreadDetails(@PathVariable(name = "slug_or_id") String slug_or_id) {
        long st = System.nanoTime();
        ThreadModel thread = threadService.getThreadBySlugOrId(slug_or_id);
        System.out.println("getThreadBySlugOrId:" + (System.nanoTime() - st));

        if (thread == null) {
            MessageModel error = new MessageModel("Can't find thread with slug or id " + slug_or_id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @PostMapping(value = "/{slug_or_id}/details")
    public ResponseEntity updateThreadData(
            @PathVariable(name = "slug_or_id") String slug,
            @RequestBody ThreadUpdateModel newThreadData
    ) {
        ThreadModel thread = threadService.getThreadBySlugOrId(slug);
        if (thread == null) {
            MessageModel error = new MessageModel("Can't find thread " + slug);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.status(HttpStatus.OK).body(threadService.updateThreadData(thread, newThreadData));
    }

    @GetMapping(value = "/{slug_or_id}/posts")
    public ResponseEntity getPosts(
            @PathVariable(name = "slug_or_id") String slug,
            @RequestParam(value = "limit", defaultValue = "0") int limit,
            @RequestParam(value = "since", defaultValue = "-1") int since,
            @RequestParam(value = "sort", defaultValue = "flat") String sort,
            @RequestParam(value = "desc", defaultValue = "false") boolean desc
    ) {
        long st = System.nanoTime();
        ThreadModel thread = threadService.getThreadBySlugOrId(slug);
        System.out.println("getThreadBySlugOrId:" + (System.nanoTime() - st));

        if (thread == null) {
            MessageModel error = new MessageModel("Can't find thread " + slug);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
         st = System.nanoTime();
        List<PostModel> posts = postService.getPosts(thread.getId(), since, desc, sort, limit);
        System.out.println("getPosts " + since + " " + desc + " " + sort + " " + limit + ":" + (System.nanoTime() - st));

        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

}
