package com.example.demo.register;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

    private RegistrationController registrationController;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationController = new RegistrationController();

        // Use reflection to set the userRepository field
        try {
            Field userRepositoryField = RegistrationController.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(registrationController, userRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void registerUser_UsernameExists() {
        // Prepare your test data and expectations
        User existingUser = new User();
        existingUser.setUsername("testUser");
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        User newUser = new User();
        newUser.setUsername("testUser");

        Model model = mock(Model.class);

        // Call the controller method
        String viewName = registrationController.registerUser(newUser, model);

        // Assert the results or view name
        assertEquals("registration", viewName);

        // Verify that the registration error message is added to the model
        verify(model).addAttribute("registrationError", "Username already exists");

        // Verify that userRepository.save is not called
        verify(userRepository, never()).save(newUser);
    }

    @Test
    void registerUser_Success() {
        // Prepare your test data and expectations
        User newUser = new User();
        newUser.setUsername("testUser");

        Model model = mock(Model.class);

        // Mock the userRepository.existsByUsername method to return false (username doesn't exist)
        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        // Call the controller method
        String viewName = registrationController.registerUser(newUser, model);

        // Assert the results or view name
        assertEquals("redirect:/welcome?username=testUser", viewName);

        // Verify that the user is saved to the database
        verify(userRepository).save(newUser);

        // Verify that no registration error message is added to the model
        verify(model, never()).addAttribute(eq("registrationError"), anyString());
    }

    @Test
    void loginUser_CorrectCredentials() {
        // Prepare your test data and expectations
        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setPassword("password");

        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("password");

        Model model = mock(Model.class);

        // Mock the userRepository.existsByUsernameAndPassword method to return true (credentials are correct)
        when(userRepository.existsByUsernameAndPassword("testUser", "password")).thenReturn(true);

        // Call the controller method
        String viewName = registrationController.loginUser(loginUser, model);

        // Assert the results or view name
        assertEquals("redirect:/welcome?username=testUser", viewName);

        // Verify that no login error message is added to the model
        verify(model, never()).addAttribute(eq("loginError"), anyString());
    }

    @Test
    void loginUser_IncorrectCredentials() {
        // Prepare your test data and expectations
        User loginUser = new User();
        loginUser.setUsername("testUser");
        loginUser.setPassword("wrongPassword");

        Model model = mock(Model.class);

        // Mock the userRepository.existsByUsernameAndPassword method to return false (credentials are incorrect)
        when(userRepository.existsByUsernameAndPassword("testUser", "wrongPassword")).thenReturn(false);

        // Call the controller method
        String viewName = registrationController.loginUser(loginUser, model);

        // Assert the results or view name
        assertEquals("login", viewName);

        // Verify that the login error message is added to the model
        verify(model).addAttribute("loginError", "Username or password incorrect");
    }
}