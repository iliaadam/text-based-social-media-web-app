package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // Add any custom queries or methods for comments here, if needed
    @Query(value = "SELECT * FROM comments WHERE post_id = :postId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Comment findLatestCommentForPost(@Param("postId") Integer postId);

    @Query(value = "SELECT * FROM comments WHERE user_id = :userID ORDER BY id DESC LIMIT 100", nativeQuery = true)
    List<Comment> findLoggedInUserComments(Integer userID);
}

