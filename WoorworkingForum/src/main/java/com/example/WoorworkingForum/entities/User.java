package com.example.WoorworkingForum.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Size(min = 3, max = 20, message = "You must enter a valid username")
    private String username;

    @Email(message = "You must enter a valid email", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty.")
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters long.")
    private String password;

    @JsonIgnoreProperties({"user", "comments"})
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics;

    @JsonIgnoreProperties({"user", "topic"})
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    private LocalDateTime accountCreation;

    private Set<String> roles;

    private boolean isBanned;

    public User() {}

    public User(Long id, String username, String email, String password, List<Topic> topics, List<Comment> comments,
                LocalDateTime accountCreation, Set<String> roles, boolean isBanned) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.topics = topics;
        this.comments = comments;
        this.accountCreation = accountCreation;
        this.roles = roles;
        this.isBanned = isBanned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public LocalDateTime getAccountCreation() {
        return accountCreation;
    }

    public void setAccountCreation(LocalDateTime accountCreation) {
        this.accountCreation = accountCreation;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
