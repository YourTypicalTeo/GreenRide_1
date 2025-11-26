// In: com.greenride.dto.LoginRequest.java
package com.greenride.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}