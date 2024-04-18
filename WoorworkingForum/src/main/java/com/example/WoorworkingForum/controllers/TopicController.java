package com.example.WoorworkingForum.controllers;


import com.example.WoorworkingForum.entities.Comment;
import com.example.WoorworkingForum.entities.Topic;
import com.example.WoorworkingForum.repositories.CommentRepository;
import com.example.WoorworkingForum.services.CommentService;
import com.example.WoorworkingForum.services.TopicService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private TopicService topicService;
    private CommentService commentService;

    @Autowired
    public TopicController(TopicService topicService, CommentService commentService) {
        this.topicService = topicService;
        this.commentService = commentService;
    }

    @GetMapping("/info")
    public String info() {
        return "Server is up and running";
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id);
    }

    @GetMapping
    public ResponseEntity<?> getTopic(@RequestParam(required = false) String title) {
        return topicService.getTopic(title);
    }

    @PostMapping("/create-topic")
    public ResponseEntity<?> addTopic(@RequestBody @Valid Topic topic,
                                      @RequestHeader("user_id") Long userId) {
        return topicService.addTopic(topic, userId);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<?> likeTopic(@PathVariable Long id,
                                       @RequestHeader("user_id") Long userId) {
            return topicService.likeTopic(id, userId);
    }

    @PostMapping("/dislike/{id}")
    public ResponseEntity<?> dislikeTopic(@PathVariable Long id,
                                          @RequestHeader("user_id") Long userId) {
            return topicService.dislikeTopic(id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id,
                                         @RequestHeader("user_id") Long userId) {
        return topicService.deleteTopic(id, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id,
                                         @RequestBody Map<String, String> updates,
                                         @RequestHeader("user_id") Long userId) {
        return topicService.updateTopic(id, updates, userId);
    }

    // -- Sorting for Topics --

    // sortBy -> {views, likes, date, last-updated, comments}
    @GetMapping("/sort")
    public ResponseEntity<?> getTopicsSorted(@RequestParam String sortBy,
                                             @RequestParam Integer sortDirection) {
        return topicService.getTopicsSorted(sortBy, sortDirection);
    }


    // ----Comments------


    @GetMapping("/{topicId}/comments")
    public ResponseEntity<?> getCommentsForTopic(@PathVariable Long topicId) {
        return commentService.getCommentsForTopic(topicId);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @PostMapping("/{topicId}/add-comment")
    public ResponseEntity<?> addComment(@PathVariable Long topicId,
                                        @RequestBody @Valid Comment comment,
                                        @RequestHeader("user_id") Long userId){
            return commentService.addComment(topicId, comment, userId);
    }

    @PostMapping("/comments/like/{id}")
    public ResponseEntity<?> likeComment(@PathVariable Long id,
                                         @RequestHeader("user_id") Long userId) {
        return commentService.likeComment(id, userId);
    }

    @PostMapping("/comments/dislike/{id}")
    public ResponseEntity<?> dislikeComment(@PathVariable Long id,
                                            @RequestHeader("user_id") Long userId) {
        return commentService.dislikeComment(id, userId);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @RequestHeader("user_id") Long userId) {
        return commentService.deleteComment(id, userId);
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @RequestBody Map<String, String> updates,
                                           @RequestHeader("user_id") Long userId) {
        return commentService.updateComment(id, updates, userId);
    }





}
