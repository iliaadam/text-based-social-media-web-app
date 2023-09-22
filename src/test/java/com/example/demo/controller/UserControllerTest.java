package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Follower;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FollowerRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userRepository, followerRepository, postRepository, commentRepository);
    }

    @Test
    void welcomePage() {
        // Prepare your test data and expectations
        Model model = mock(Model.class);
        User loggedInUser = new User();
        loggedInUser.setUsername("testUser");

        // Mock any required repository method calls
        when(userRepository.findByUsername("testUser")).thenReturn(loggedInUser);
        // Mock other repository calls as needed

        // Mock the behavior of repositories for retrieving posts, followers, comments, etc.
        List<Post> userPosts = new ArrayList<>(); // Create mock user posts
        List<User> followers = new ArrayList<>(); // Create mock followers
        List<Comment> loggedInUserComments = new ArrayList<>(); // Create mock comments

        when(postRepository.findByUser(loggedInUser)).thenReturn(userPosts);
        when(userRepository.findFollowers(loggedInUser.getId())).thenReturn(followers);
        when(commentRepository.findLoggedInUserComments(loggedInUser.getId())).thenReturn(loggedInUserComments);

        // Call the controller method
        String viewName = userController.welcomePage("testUser", model);

        // Assert the results or view name
        assertEquals("welcome", viewName);

        // Verify that the model attributes are set as expected
        verify(model).addAttribute("username", "testUser");
        verify(model).addAttribute("loggedInUser", loggedInUser);
        verify(model).addAttribute("posts", userPosts);
        verify(model).addAttribute("followersUsers", followers);
        verify(model).addAttribute("loggedInUserComments", loggedInUserComments);
        // You can add more verification for other model attributes
    }

    @Test
    void followUser() {
        // Prepare your test data and expectations
        Integer followerId = 1;
        Integer followeeId = 2;
        User follower = new User();
        User followee = new User();
        when(userRepository.findById(followerId)).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followeeId)).thenReturn(java.util.Optional.of(followee));

        // Mock the behavior of followerRepository.save to accept any Follower object
        when(followerRepository.save(any(Follower.class))).thenReturn(new Follower());

        // Call the controller method
        String viewName = userController.followUser(followerId, followeeId);

        // Assert the results or view name
        assertEquals("redirect:/welcome?username=" + follower.getUsername(), viewName);

        // Verify that the Follower record is saved with any Follower object
        verify(followerRepository).save(any(Follower.class));
    }

    @Test
    void unfollowUser() {
        // Prepare your test data and expectations
        Integer followerId = 1;
        Integer followeeId = 2;
        User follower = new User();
        User followee = new User();
        when(userRepository.findById(followerId)).thenReturn(java.util.Optional.of(follower));
        when(userRepository.findById(followeeId)).thenReturn(java.util.Optional.of(followee));
        Follower existingFollower = new Follower();
        existingFollower.setFollower(follower);
        existingFollower.setFollowee(followee);
        when(followerRepository.findByFollowerAndFollowee(follower, followee)).thenReturn(existingFollower);

        // Call the controller method
        String viewName = userController.unfollowUser(followerId, followeeId);

        // Assert the results or view name
        assertEquals("redirect:/welcome?username=" + follower.getUsername(), viewName);

        // Verify that the Follower record is deleted
        verify(followerRepository).delete(existingFollower);
    }

    @Test
    void submitComment() {
        // Prepare your test data and expectations
        Integer userId = 1;
        Integer postId = 2;
        String content = "Test comment content";
        User user = new User();
        user.setId(userId);
        Post post = new Post();
        post.setId(postId);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(java.util.Optional.of(post));

        // Call the controller method
        String viewName = userController.submitComment(userId, postId, content);

        // Assert the results or view name
        assertEquals("redirect:/welcome?username=" + user.getUsername(), viewName);

        // Verify that the Comment record is saved
        verify(commentRepository).save(commentCaptor.capture());

        // Get the captured Comment object
        Comment capturedComment = commentCaptor.getValue();

        // Assert that the attributes of the captured Comment match the expected values
        assertEquals(user, capturedComment.getUser());
        assertEquals(post, capturedComment.getPost());
        assertEquals(content, capturedComment.getContent());
    }
}