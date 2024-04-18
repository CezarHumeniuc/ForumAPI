package com.example.WoorworkingForum.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "topics")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Topic title cannot be empty")
    private String title;

    @NotBlank(message = "Topic body cannot be empty")
    private String content;

    private LocalDateTime timeOfPosting;

    private LocalDateTime lastUpdated;

    @JsonIgnoreProperties("topic")
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("topics")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private int likes;

    private int dislikes;

    private int views;

    private int nrOfComments;

    public Topic() {
    }

    public Topic(Long id, String title, String content, LocalDateTime timeOfPosting, List<Comment> comments,
                 LocalDateTime lastUpdated, User user, int likes, int views, int dislikes, int nrOfComments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timeOfPosting = timeOfPosting;
        this.comments = comments;
        this.lastUpdated = lastUpdated;
        this.likes = likes;
        this.dislikes = dislikes;
        this.user = user;
        this.views = views;
        this.nrOfComments = nrOfComments;
    }

    public int getNrOfComments() {
        if(comments.isEmpty()) {
            return 0;
        }else {
            return comments.size();
        }
    }

    public void setNrOfComments(int nrOfComments) {
        this.nrOfComments = nrOfComments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setTopic(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTopic(null);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeOfPosting() {
        return timeOfPosting;
    }

    public void setTimeOfPosting(LocalDateTime timeOfPosting) {
        this.timeOfPosting = timeOfPosting;
    }
}
