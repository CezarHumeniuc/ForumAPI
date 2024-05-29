package com.example.WoorworkingForum.services;

import com.example.WoorworkingForum.entities.Topic;
import com.example.WoorworkingForum.entities.User;
import com.example.WoorworkingForum.helpers.CustomMessages;
import com.example.WoorworkingForum.repositories.CommentRepository;
import com.example.WoorworkingForum.repositories.TopicRepository;
import com.example.WoorworkingForum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class TopicService {

    private TopicRepository topicRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @Autowired
    public TopicService (TopicRepository topicRepository, CommentRepository commentRepository,
                         UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    //Checks if user is authorized to post / like
    public boolean userValidCheck(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent() && !user.get().isBanned()) return true;
        return false;
    }

    public ResponseEntity<?> getTopicById(Long id) {
        ResponseEntity<?> response = null;

        try {
            Optional<Topic> topic = topicRepository.findById(id);

            if (topic.isPresent()) {
                int views = topic.get().getViews() + 1;
                topic.get().setViews(views);
                Topic topicA = topicRepository.saveAndFlush(topic.get());

                response = new ResponseEntity<>(topicA, HttpStatus.FOUND);
            } else {
                response = new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> getTopic(String title) {
        ResponseEntity<?> response = null;

        try {
            if (title != null) {

                Optional<List<Topic>> topicList = topicRepository.findByTitle(title);

                if (topicList.isEmpty()) {
                    response = new ResponseEntity<>("Topic Not Found", HttpStatus.NOT_FOUND);
                } else {
                    response = new ResponseEntity<>(topicList, HttpStatus.FOUND);
                }

            } else {
                response = new ResponseEntity<>(topicRepository.findAll(), HttpStatus.FOUND);
            }

        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return response;
    }

    public ResponseEntity<?> addTopic(Topic topic, Long userId){
        ResponseEntity<?> response = null;

        try {
            if (userValidCheck(userId)) {

                User user = userRepository.findById(userId).get();
                topic.setUser(user);
                topic.setTimeOfPosting(LocalDateTime.now());
                topic.setLastUpdated(LocalDateTime.now());
                topic.setLikes(1);
                topic.setViews(0);
                user.getTopics().add(topic);
                Topic addedTopic = topicRepository.save(topic);
                addedTopic.setComments(new ArrayList<>());

                response = new ResponseEntity<>(addedTopic, HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> deleteTopic(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<User> user = userRepository.findById(userId);
            Optional<Topic> topic = topicRepository.findById(id);

            if (userValidCheck(userId)) {
                if (topic.isPresent()) {
                    if (user.get().getRoles().contains("Admin") ||
                            user.get().getTopics().contains(topic.get())) {   // Checks if user is admin or poster.

                        user.get().getTopics().remove(topic.get());
                        userRepository.saveAndFlush(user.get());
                        topicRepository.deleteById(id);

                        response = new ResponseEntity<>("Topic deleted", HttpStatus.OK);

                    }else {
                        response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }

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

    public ResponseEntity<?> updateTopic(Long id, Map<String, String> updates, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<Topic> topic  = topicRepository.findById(id);
            Optional<User> user = userRepository.findById(userId);

            if (topic.isPresent()) {
                if (userValidCheck(userId)) {
                    if (user.get().getRoles().contains("Admin") ||
                            user.get().getTopics().contains(topic.get())) {

                        Topic topicUpdate = topicRepository.findById(id).get();

                        if (updates.containsKey("title")) {
                            topicUpdate.setTitle(updates.get("title"));
                        }
                        if (updates.containsKey("content")) {
                            topicUpdate.setContent(updates.get("content"));
                        }

                        topicUpdate.setLastUpdated(LocalDateTime.now());
                        topicUpdate = topicRepository.saveAndFlush(topicUpdate);

                        response = new ResponseEntity<>(topicUpdate, HttpStatus.OK);

                    } else {
                        response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }


    public ResponseEntity<?> likeTopic(Long id, Long userId){
        ResponseEntity<?> response = null;

        try {
            if (userValidCheck(userId)) {
                Optional<Topic> topic = topicRepository.findById(id);

                if (topic.isPresent()) {
                    int likes = topic.get().getLikes() + 1;
                    topic.get().setLikes(likes);
                    Topic updatedTopic = topicRepository.saveAndFlush(topic.get());

                    response = new ResponseEntity<>(updatedTopic.getLikes(), HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>("Topic not found", HttpStatus.NOT_FOUND);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseEntity<?> dislikeTopic(Long id, Long userId){
        ResponseEntity<?> response = null;

        try {
            if (userValidCheck(userId)) {
                Optional<Topic> topic = topicRepository.findById(id);

                if (topic.isPresent()) {
                    int dislikes = topic.get().getDislikes() + 1;
                    topic.get().setDislikes(dislikes);
                    Topic updatedTopic = topicRepository.saveAndFlush(topic.get());

                    response = new ResponseEntity<>(updatedTopic.getDislikes(), HttpStatus.OK);
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

    public ResponseEntity<?> getTopicsSorted(String sortBy, Integer sortDirection) {
        ResponseEntity<?> response = null;

        try {
            List<Topic> sortedTopics = new ArrayList<>();

            if (sortDirection == 1) {
                if (sortBy.equalsIgnoreCase("likes")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "likes"));
                }else if (sortBy.equalsIgnoreCase("views")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "views"));
                }else if (sortBy.equalsIgnoreCase("date")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "timeOfPosting"));
                }else if (sortBy.equalsIgnoreCase("last-updated")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "lastUpdated"));
                }else if(sortBy.equalsIgnoreCase("comments")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.ASC, "nrOfComments"));
                }

            }else if (sortDirection == - 1) {
                if (sortBy.equalsIgnoreCase("likes")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.DESC, "likes"));
                }else if (sortBy.equalsIgnoreCase("views")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.DESC, "views"));
                }else if (sortBy.equalsIgnoreCase("date")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.DESC, "timeOfPosting"));
                }else if (sortBy.equalsIgnoreCase("last-updated")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.DESC, "lastUpdated"));
                }else if(sortBy.equalsIgnoreCase("comments")) {
                    sortedTopics = topicRepository.findAll(Sort.by(Sort.Direction.DESC, "nrOfComments"));
                }
            }
            response = new ResponseEntity<>(sortedTopics, HttpStatus.OK);

        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
