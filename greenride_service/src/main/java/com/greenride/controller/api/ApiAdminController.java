package com.greenride.controller.api;

import com.greenride.repository.BookingRepository;
import com.greenride.repository.RideRepository;
import com.greenride.repository.UserRepository;
import com.greenride.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ApiAdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private BookingRepository bookingRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRidesOffered", rideRepository.count());
        stats.put("totalBookingsMade", bookingRepository.count());
        return ResponseEntity.ok(stats);
    }

    // List all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Ban/Unban User
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEnabled(!user.isEnabled()); // Toggle status
        userRepository.save(user);

        return ResponseEntity.ok("User status updated. Enabled: " + user.isEnabled());
    }
}