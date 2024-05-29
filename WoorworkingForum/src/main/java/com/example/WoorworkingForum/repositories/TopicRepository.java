package com.example.WoorworkingForum.repositories;


import com.example.WoorworkingForum.entities.Topic;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<List<Topic>> findByTitle(String title);
}
