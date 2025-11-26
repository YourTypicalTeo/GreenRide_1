package com.greenride.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebAuthController {

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request, Model model) {
        // If already logged in, redirect to home
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }

        // Handle errors from Spring Security (e.g., Bad Credentials)
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return "login"; // Returns login.html
    }
}