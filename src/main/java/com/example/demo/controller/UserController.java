package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Follower;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FollowerRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserController {


    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserController(UserRepository userRepository, FollowerRepository followerRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/")
    public String initialPage() {
        return "initial";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/welcome")
    public String welcomePage(@RequestParam(name = "username") String username, Model model) {
        model.addAttribute("username", username);

        // Retrieve all usernames from the users table
        Iterable<User> users = userRepository.findAll();

        // Extract usernames and add them to the model
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        usernames.remove(username);
        model.addAttribute("usernames", usernames);

        // Retrieve the logged-in user
        User loggedInUser = userRepository.findByUsername(username);

        // Retrieve the followers (users who are following the logged-in user)
        List<User> followers = userRepository.findFollowers(loggedInUser.getId());
        model.addAttribute("followersUsers", followers);
        // Retrieve the followees (users who the logged-in user is following)
        List<User> followees = userRepository.findFollowees(loggedInUser.getId());

        // Calculate users that the logged-in user is not following
        List<User> notFollowing = new ArrayList<>((Collection) users);
        notFollowing.remove(loggedInUser); // Remove the logged-in user from the list
        notFollowing.removeAll(followers); // Remove the users the logged-in user is following

        // Extract usernames and add them to the model
        List<String> followerUsernames = followers.stream().map(User::getUsername).collect(Collectors.toList());
        List<String> followeeUsernames = followees.stream().map(User::getUsername).collect(Collectors.toList());


        // Retrieve the posts for the logged-in user
        List<Post> userPosts = postRepository.findByUser(loggedInUser);
        model.addAttribute("loggedInUser", loggedInUser);

        // Iterate through userPosts and update each post with the latest comment
        for (Post userPost : userPosts) {
            // Retrieve the latest comment for the current post
            Comment latestComment = commentRepository.findLatestCommentForPost(userPost.getId());

            // Update the post with the latest comment
            userPost.setLatestComment(latestComment);
        }
        model.addAttribute("posts", userPosts);


        // Create a list to store posts from followers
        List<Post> postsFromFollowers = new ArrayList<>();

        for (User follower : followers) {
            List<Post> followerPosts = postRepository.findByUser(follower);
            for (Post followerPost : followerPosts) {
                // Retrieve the latest comment for each post
                Comment latestComment = commentRepository.findLatestCommentForPost(followerPost.getId());
                followerPost.setLatestComment(latestComment); // Update the post with the latest comment
            }
            postsFromFollowers.addAll(followerPosts);
        }

        model.addAttribute("postsFromFollowers", postsFromFollowers);

        model.addAttribute("followers", followerUsernames);
        model.addAttribute("followees", followeeUsernames);
        model.addAttribute("notFollowing", notFollowing);

        // Retrieve the logged-in user's comments sorted by comment ID in descending order
        List<Comment> loggedInUserComments = commentRepository.findLoggedInUserComments(loggedInUser.getId());

        // Add the user's comments to the model
        model.addAttribute("loggedInUserComments", loggedInUserComments);

        return "welcome";
    }


    @PostMapping("/follow")
    public String followUser(@RequestParam(name = "followerId") Integer followerId,
                             @RequestParam(name = "followeeId") Integer followeeId) {
        // Retrieve the logged-in user and the user to follow
        User follower = userRepository.findById(followerId).orElse(null);
        User followee = userRepository.findById(followeeId).orElse(null);

        if (follower != null && followee != null) {
            // Create a new Follower record
            Follower newFollower = new Follower();
            newFollower.setFollower(follower);
            newFollower.setFollowee(followee);

            // Save the Follower record
            followerRepository.save(newFollower);
        }

        return "redirect:/welcome?username=" + follower.getUsername();
    }

    @PostMapping("/unfollow")
    public String unfollowUser(@RequestParam(name = "followerId") Integer followerId,
                               @RequestParam(name = "followeeId") Integer followeeId) {
        // Retrieve the logged-in user and the user to unfollow
        User follower = userRepository.findById(followerId).orElse(null);
        User followee = userRepository.findById(followeeId).orElse(null);

        if (follower != null && followee != null) {
            // Find and delete the Follower record if it exists
            Follower existingFollower = followerRepository.findByFollowerAndFollowee(follower, followee);
            if (existingFollower != null) {
                followerRepository.delete(existingFollower);
            }
        }

        return "redirect:/welcome?username=" + follower.getUsername();
    }


    @PostMapping("/submitPost")
    public String submitPost(@RequestParam(name = "userId") Integer userId, @RequestParam(name = "content") String content) {
        // Create a new Post object
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle the case where the user with the provided ID is not found
            // You can redirect to an error page or perform any other error handling here
            return "redirect:/error";
        }

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);

        // Save the post to the database
        postRepository.save(post);

        // Redirect to the page where you want to display posts
        return "redirect:/welcome?username=" + user.getUsername();
    }

    @PostMapping("/submitComment")
    public String submitComment(
            @RequestParam(name = "userId") Integer userId,
            @RequestParam(name = "postId") Integer postId,
            @RequestParam(name = "content") String content
    ) {
        // Retrieve the user and post based on their IDs
        User user = userRepository.findById(userId).orElse(null);
        Post post = postRepository.findById(postId).orElse(null);

        if (user == null || post == null) {
            // Handle the case where the user or post with the provided IDs is not found
            // You can redirect to an error page or perform any other error handling here
            return "redirect:/error";
        }

        // Create a new Comment object
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        // Save the comment to the database
        commentRepository.save(comment);

        // Redirect back to the post's page or wherever you want to display comments
        return "redirect:/welcome?username=" + user.getUsername();
    }


}

