package com.greenride.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotBlank
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must start with + and country code (e.g. +3069...)")
        String phoneNumber
) {}