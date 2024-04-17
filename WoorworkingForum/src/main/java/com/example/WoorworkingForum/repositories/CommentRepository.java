package com.example.WoorworkingForum.repositories;

import com.example.WoorworkingForum.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
