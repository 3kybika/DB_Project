package db.demo.services;

import db.demo.views.StatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServantService {
    private ForumService forumService;
    private PostService postService;
    private ThreadService threadService;
    private UserService userService;
    private VoteService voteService;

    @Autowired
    public ServantService(
            ForumService forumService,
            UserService userService,
            ThreadService threadService,
            PostService postService,
            VoteService voteService
    ) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
        this.postService = postService;
        this.voteService = voteService;
        //ToDo: Не забыть убрать
        /*try {
            clearDatabase();
        } catch (Exception e){

        }*/
    }

    public StatusModel getStatus() {
        StatusModel status = new StatusModel();

        status.setForum(forumService.getCount());
        status.setPost(postService.getCount());
        status.setThread(threadService.getCount());
        status.setUser(userService.getCount());

        return status;
    }

    public void clearDatabase() {
        forumService.clear();
        postService.clear();
        threadService.clear();
        userService.clear();
        voteService.clear();
    }

}
