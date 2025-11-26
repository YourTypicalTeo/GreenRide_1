package com.greenride.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebHomeController {

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/rides"; // We will build this page next
        }
        return "index";
    }

    @GetMapping("/logout-confirm")
    public String logoutConfirm() {
        return "logout";
    }
}