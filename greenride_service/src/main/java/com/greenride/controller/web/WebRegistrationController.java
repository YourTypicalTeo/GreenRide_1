package com.greenride.controller.web;

import com.greenride.dto.RegisterRequest;
import com.greenride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebRegistrationController {

    private final UserService userService;

    @Autowired
    public WebRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Authentication authentication, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }
        // Initialize DTO with 4 empty strings (username, email, password, phone)
        model.addAttribute("registerRequest", new RegisterRequest("", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(
            Authentication authentication,
            @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            Model model) {

        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }

        try {
            userService.registerUser(registerRequest);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerRequest", registerRequest);
            return "register";
        }
    }
}