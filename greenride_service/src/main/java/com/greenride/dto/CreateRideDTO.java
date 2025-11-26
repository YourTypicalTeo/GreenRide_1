// In: com.greenride.dto.CreateRideDTO.java
package com.greenride.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateRideDTO(
        @NotBlank String startLocation,
        @NotBlank String destination,
        @Future LocalDateTime departureTime,
        @Min(1) int availableSeats
) {}