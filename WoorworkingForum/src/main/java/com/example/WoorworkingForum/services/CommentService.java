package com.example.WoorworkingForum.services;

import com.example.WoorworkingForum.entities.Comment;
import com.example.WoorworkingForum.entities.Topic;
import com.example.WoorworkingForum.entities.User;
import com.example.WoorworkingForum.helpers.CustomMessages;
import com.example.WoorworkingForum.repositories.CommentRepository;
import com.example.WoorworkingForum.repositories.TopicRepository;
import com.example.WoorworkingForum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository commentRepository;
    private TopicRepository topicRepository;
    private UserRepository userRepository;

    @Autowired
    public CommentService (CommentRepository commentRepository, TopicRepository topicRepository,
                           UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    public boolean userValidCheck(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() && !user.get().isBanned();
    }

    public ResponseEntity<?> addComment(Long topicId, Comment comment, Long userId){
        ResponseEntity<?> response = null;

        try {
            if (userValidCheck(userId)) {
                Optional<Topic> topic = topicRepository.findById(topicId);
                User user = userRepository.findById(userId).get();

                if (topic.isPresent()) {
                    comment.setTimeOfPosting(LocalDateTime.now());
                    comment.setTopic(topic.get());
                    comment.setUser(user);

                    topic.get().addComment(comment);
                    topic.get().setLastUpdated(LocalDateTime.now());

                    user.getComments().add(comment);

                    topicRepository.saveAndFlush(topic.get());
                    userRepository.saveAndFlush(user);
                    Comment addedComment = commentRepository.saveAndFlush(comment);

                    response = new ResponseEntity<>(addedComment, HttpStatus.CREATED);

                } else {
                    response = new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
                }
            } else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> getCommentById(Long id) {
        ResponseEntity<?> response = null;

        try {
            if (commentRepository.existsById(id)) {
                Comment foundComment = commentRepository.findById(id).get();
                response = new ResponseEntity<>(foundComment, HttpStatus.FOUND);
            } else {
                response = new ResponseEntity<>("Comment was not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> getCommentsForTopic(Long topicId) {
        ResponseEntity<?> response = null;

        try{
            if(topicRepository.existsById(topicId)) {
                Topic topic = topicRepository.findById(topicId).get();

                if (!topic.getComments().isEmpty() && !(topic.getComments() == null)) {
                    response = new ResponseEntity<>(topic.getComments(), HttpStatus.FOUND);
                }else {
                    response = new ResponseEntity<>("No comments for this thread", HttpStatus.NO_CONTENT);
                }
            } else {
                response = new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;

    }

    public ResponseEntity<?> deleteComment(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<User> user = userRepository.findById(userId);
            Optional<Comment> comment = commentRepository.findById(id);

            if(userValidCheck(userId)) {
                if (comment.isPresent()) {
                    if(user.get().getRoles().contains("Admin") ||
                            user.get().getComments().contains(comment.get())) {

                        user.get().getComments().remove(comment.get());
                        userRepository.saveAndFlush(user.get());
                        commentRepository.deleteById(id);
                        response = new ResponseEntity<>("Comment was deleted", HttpStatus.OK);

                    } else {
                        response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    response = new ResponseEntity<>("Comment was not found", HttpStatus.NOT_FOUND);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;

    }

    public ResponseEntity<?> updateComment(Long id, Map<String, String> updates, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<Comment> comment = commentRepository.findById(id);
            Optional<User> user = userRepository.findById(userId);

            if (comment.isPresent()) {
                if (userValidCheck(userId)) {
                    if (user.get().getRoles().contains("Admin") ||
                            user.get().getComments().contains(comment.get())) {

                        if (updates.containsKey("content")) {
                            Comment updateComment = commentRepository.findById(id).get();
                            updateComment.setContent(updates.get("content"));
                            updateComment = commentRepository.saveAndFlush(updateComment);

                            response = new ResponseEntity<>(updateComment, HttpStatus.OK);

                        } else {
                            response = new ResponseEntity<>("Illegal update", HttpStatus.BAD_REQUEST);
                        }
                    }else {
                        response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }else {
                    response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;

    }

    public ResponseEntity<?> likeComment(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try{
            if (userValidCheck(userId)){
                Optional<Comment> comment = commentRepository.findById(id);

                if (comment.isPresent()) {
                    int likes = comment.get().getLikes() + 1;
                    comment.get().setLikes(likes);
                    Comment updatedComment = commentRepository.saveAndFlush(comment.get());

                    response = new ResponseEntity<>(updatedComment, HttpStatus.OK);

                } else {
                    response = new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> dislikeComment(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try {
            if (userValidCheck(userId)) {
                Optional<Comment> comment = commentRepository.findById(id);

                if(comment.isPresent()) {
                    int dislikes = comment.get().getDislikes() + 1;
                    comment.get().setDislikes(dislikes);
                    Comment updatedComment = commentRepository.saveAndFlush(comment.get());

                    response = new ResponseEntity<>(updatedComment, HttpStatus.OK);
                }else {
                    response = new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
