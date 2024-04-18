package com.example.WoorworkingForum.services;

import com.example.WoorworkingForum.entities.User;
import com.example.WoorworkingForum.helpers.CustomMessages;
import com.example.WoorworkingForum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public ResponseEntity<?> getUserById(Long id) {
        ResponseEntity<?> response = null;

        try{
            Optional<?> user = userRepository.findById(id);

            if (user.isPresent()) {
                response = new ResponseEntity<>(user.get(), HttpStatus.FOUND);
            }else {
                response = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> addUser(User user) {
        ResponseEntity<?> response = null;
        try {

            user.setAccountCreation(LocalDateTime.now());
            user.setRoles(new HashSet<>());
            user.getRoles().add("User");
            User newUser = userRepository.saveAndFlush(user);
            response = new ResponseEntity<>(newUser, HttpStatus.CREATED);

        }catch(DataIntegrityViolationException e) {
            response = new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);

        }catch(Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> getUser(String username) {
        ResponseEntity<?> response = null;

        try{
            if (username != null) {

                Optional<User> user = userRepository.findByUsername(username);

                if (user.isPresent()) {
                    response = new ResponseEntity<>(user.get(), HttpStatus.OK);
                }else {
                    response = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }

            } else {
                response = new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> updateUser(Long id, Map<String, String> updates, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<User> user = userRepository.findById(id);
            Optional<User> user2 = userRepository.findById(userId);

            if ((user.isPresent() && id.equals(userId)) || user2.get().getRoles().contains("Admin")) {
                String message = "";
                if (updates.containsKey("username")) {
                    user.get().setUsername(updates.get("username"));
                }
                if (updates.containsKey("email")) {
                    user.get().setEmail(updates.get("email"));
                }
                if (updates.containsKey("password")) {
                    user.get().setPassword(updates.get("password"));
                }
                User updatedUser = userRepository.saveAndFlush(user.get());

                response = new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }catch (DataIntegrityViolationException e) {
            response = new ResponseEntity<>("Username or email already in use", HttpStatus.NOT_ACCEPTABLE);
        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.NOT_FOUND);
        }

        return response;
    }

    public ResponseEntity<?> deleteUser(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try{
            Optional<User> user = userRepository.findById(id);

            if (user.isPresent()) {
                if (id.equals(userId) || user.get().getRoles().contains("Admin")) {
                    userRepository.deleteById(id);
                    response = new ResponseEntity<>("User removed", HttpStatus.OK);
                }else {
                    response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            }else {
                response = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

        }catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    public ResponseEntity<?> makeAdmin(Long id) {
        ResponseEntity<?> response = null;

        try{
            Optional<User> user = userRepository.findById(id);

            if (user.isPresent()) {
                user.get().getRoles().add("Admin");
                User admin = userRepository.saveAndFlush(user.get());

                response = new ResponseEntity<>(admin, HttpStatus.OK);
            }else {
                response = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public ResponseEntity<?> banUser(Long id, Long userId) {
        ResponseEntity<?> response = null;

        try {
            Optional<User> user = userRepository.findById(id);
            Optional<User> admin = userRepository.findById(userId);

            if (admin.isPresent()) {
                if (user.isPresent()){
                    user.get().setBanned(true);
                    User bannedUser = userRepository.saveAndFlush(user.get());
                    response = new ResponseEntity<>(bannedUser, HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                }
            }else {
                response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            response = new ResponseEntity<>(CustomMessages.INTERNAL_SERVER_ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
