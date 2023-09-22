package com.example.demo.register;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        // Check if the username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("registrationError", "Username already exists");
            return "registration";
        }

        // Save the user to the database
        userRepository.save(user);
        return "redirect:/welcome?username=" + user.getUsername();
    }

    @PostMapping("/login")
    public String loginUser(User user, Model model) {
        // Check if the user with the given credentials exists
        if (!userRepository.existsByUsernameAndPassword(user.getUsername(), user.getPassword())) {
            model.addAttribute("loginError", "Username or password incorrect");
            return "login";
        }

        return "redirect:/welcome?username=" + user.getUsername();
    }
}



