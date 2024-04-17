package com.example.WoorworkingForum.controllers;


import com.example.WoorworkingForum.entities.User;
import com.example.WoorworkingForum.services.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam(required = false) String username){
        return userService.getUser(username);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody Map<String, String> updates,
                                        @RequestHeader("user_id") Long userId) {
        return userService.updateUser(id, updates, userId);
    }

    @PatchMapping("/make-admin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable Long id) {
        return userService.makeAdmin(id);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<?> banUser(@PathVariable Long id,
                                     @RequestHeader("user_id") Long userId){
        return userService.banUser(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id,
                                        @RequestHeader("user_id") Long userId) {
        return userService.deleteUser(id, userId);
    }



}
