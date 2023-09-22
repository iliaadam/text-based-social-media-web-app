package com.example.demo.repository;

import com.example.demo.entity.Follower;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    Follower findByFollowerAndFollowee(User follower, User followee);
    // Add any custom queries or methods for followers here, if needed
}

