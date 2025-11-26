package com.greenride.dto;

public record UserView(
        Long id,
        String username,
        String email
) {}