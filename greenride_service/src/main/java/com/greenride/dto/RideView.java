package com.greenride.dto;

import java.time.LocalDateTime;

public record RideView(
        Long id,
        String startLocation,
        String destination,
        LocalDateTime departureTime,
        int availableSeats,
        UserView driver
) {}