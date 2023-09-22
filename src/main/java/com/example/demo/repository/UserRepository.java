package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    boolean existsByUsernameAndPassword(String username, String password);

    User findByUsername(String username);

    // Custom method to find followers
    @Query("SELECT u FROM User u JOIN u.followers f WHERE f.id = :userId")
    List<User> findFollowers(@Param("userId") Integer userId);

    // Custom method to find followees
    @Query("SELECT u FROM User u JOIN u.followees f WHERE f.id = :userId")
    List<User> findFollowees(@Param("userId") Integer userId);

    /* Custom method to find users not followed by the logged-in user
    @Query("SELECT u FROM User u WHERE u.id <> :userId AND u NOT IN (SELECT f.id FROM User u2 JOIN u2.followees f WHERE u2.id = :userId)")
    List<User> findUsersNotFollowedBy(@Param("userId") Integer userId);
   */
}
