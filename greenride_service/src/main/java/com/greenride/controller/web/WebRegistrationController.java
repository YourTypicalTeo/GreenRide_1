package com.greenride.controller.web;

import com.greenride.dto.RegisterRequest;
import com.greenride.service.UserService;
import jakarta.validation.Valid; // <--- Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // <--- Import
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
        model.addAttribute("registerRequest", new RegisterRequest("", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(
            Authentication authentication,
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, // <--- Added @Valid
            BindingResult bindingResult, // <--- Added BindingResult
            Model model) {

        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }

        // Fix: If password is too short, return to form with errors
        if (bindingResult.hasErrors()) {
            return "register";
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