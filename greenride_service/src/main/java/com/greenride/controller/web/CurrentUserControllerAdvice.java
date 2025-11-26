package com.greenride.controller.web;

import com.greenride.security.CurrentUser;
import com.greenride.security.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserControllerAdvice(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @ModelAttribute("currentUser")
    public CurrentUser getCurrentUser() {
        return currentUserProvider.getCurrentUser().orElse(null);
    }
}