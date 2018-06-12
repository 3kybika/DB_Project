package db.demo.controllers;

import db.demo.services.UserService;
import db.demo.views.MessageModel;
import db.demo.views.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/user")
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/{nickname}/create")
    public ResponseEntity createUser(
            @PathVariable(name = "nickname") String nickname,
            @RequestBody UserModel user
    ) {
        user.setNickname(nickname);

        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateKeyException e) {
            List<UserModel> result = userService.getDublicateUsers(user);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }


    @GetMapping(path = "/{nickname}/profile")
    public ResponseEntity getProfile(
            @PathVariable(name = "nickname") String nickname
    ) {
        UserModel user = userService.getUserByNickname(nickname);
        if (user == null) {
            MessageModel error = new MessageModel("Can't find user with nickname " + nickname);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(path = "/{nickname}/profile")
    public ResponseEntity updateUser(
            @PathVariable(name = "nickname") String nickname,
            @RequestBody UserModel user
    ) {
        user.setNickname(nickname);
        try {

            UserModel result = userService.updateUserInfo(user);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (EmptyResultDataAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageModel("User not found!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageModel("Not unique email"));
        }
    }

}
