package com.greenride.controller.api;

import com.greenride.dto.CreateRideDTO;
import com.greenride.dto.RideView;
import com.greenride.model.Ride;
import com.greenride.security.CurrentUserProvider;
import com.greenride.service.BookingService;
import com.greenride.service.RideService;
import com.greenride.service.mapper.RideMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rides")
@Tag(name = "Rides & Bookings", description = "Endpoints for managing rides and bookings")
@SecurityRequirement(name = "Bearer Authentication")
public class ApiRideController {

    private final RideService rideService;
    private final BookingService bookingService;
    private final CurrentUserProvider currentUserProvider;
    private final RideMapper rideMapper;

    @Autowired
    public ApiRideController(RideService rideService,
                             BookingService bookingService,
                             CurrentUserProvider currentUserProvider,
                             RideMapper rideMapper) {
        this.rideService = rideService;
        this.bookingService = bookingService;
        this.currentUserProvider = currentUserProvider;
        this.rideMapper = rideMapper;
    }

    private String getCurrentUsername() {
        return currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error: User is not authenticated."))
                .username();
    }

    @Operation(summary = "Create a new Ride")
    @PostMapping
    public ResponseEntity<?> createRide(@Valid @RequestBody CreateRideDTO createRideDto) {
        String username = getCurrentUsername();
        Ride ride = rideService.createRide(createRideDto, username);
        return ResponseEntity.status(201).body("Ride created successfully with ID: " + ride.getId());
    }

    @Operation(summary = "Search for Rides")
    @GetMapping("/search")
    public ResponseEntity<List<RideView>> searchRides(
            @Parameter(description = "Starting city") @RequestParam String start,
            @Parameter(description = "Destination city") @RequestParam String dest) {

        List<Ride> rides = rideService.searchRides(start, dest);

        List<RideView> views = rides.stream()
                .map(rideMapper::toRideView)
                .collect(Collectors.toList());

        return ResponseEntity.ok(views);
    }

    @Operation(summary = "Get Ride Details")
    @GetMapping("/{rideId}")
    public ResponseEntity<Map<String, Object>> getRideDetails(@PathVariable Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        // Cleaned up: No WeatherService call here anymore
        return ResponseEntity.ok(Map.of("ride", rideMapper.toRideView(ride)));
    }

    @Operation(summary = "Book a Seat")
    @PostMapping("/{rideId}/bookings")
    public ResponseEntity<String> bookSeat(@PathVariable Long rideId) {
        bookingService.bookRide(rideId, getCurrentUsername());
        return ResponseEntity.ok("Booking confirmed!");
    }

    @Operation(summary = "Cancel Booking")
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId, getCurrentUsername());
        return ResponseEntity.ok("Booking cancelled successfully. Seat restored.");
    }

    @Operation(summary = "View Offered Rides")
    @GetMapping("/my-offered-rides")
    public ResponseEntity<List<RideView>> getMyOfferedRides() {
        List<Ride> rides = rideService.getRidesByDriver(getCurrentUsername());
        List<RideView> views = rides.stream()
                .map(rideMapper::toRideView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(views);
    }

    @Operation(summary = "View My Bookings")
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings(getCurrentUsername()));
    }
}