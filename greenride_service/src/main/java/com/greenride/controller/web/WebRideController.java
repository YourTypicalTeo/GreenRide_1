package com.greenride.controller.web;

import com.greenride.dto.CreateRideDTO;
import com.greenride.dto.RideView;
import com.greenride.model.Ride;
import com.greenride.security.CurrentUserProvider;
import com.greenride.service.BookingService;
import com.greenride.service.RideService;
import com.greenride.service.mapper.RideMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebRideController {

    private final RideService rideService;
    private final BookingService bookingService;
    private final RideMapper rideMapper;
    private final CurrentUserProvider currentUserProvider;

    @Autowired
    public WebRideController(RideService rideService,
                             BookingService bookingService,
                             RideMapper rideMapper,
                             CurrentUserProvider currentUserProvider) {
        this.rideService = rideService;
        this.bookingService = bookingService;
        this.rideMapper = rideMapper;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Show the Ride Dashboard (Search Page).
     * Default shows all rides if no search params are provided.
     */
    @GetMapping("/rides")
    public String showRides(
            @RequestParam(required = false, defaultValue = "") String start,
            @RequestParam(required = false, defaultValue = "") String dest,
            Model model) {

        // Search logic: If params are empty, this acts as "Find All"
        List<Ride> rides = rideService.searchRides(start, dest);

        // Convert to View DTOs
        List<RideView> rideViews = rides.stream()
                .map(rideMapper::toRideView)
                .collect(Collectors.toList());

        model.addAttribute("rides", rideViews);
        model.addAttribute("paramStart", start); // Keep the search term in the box
        model.addAttribute("paramDest", dest);

        return "rides";
    }

    /**
     * Show the form to create a new ride.
     */
    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model) {
        // Pre-fill the form with defaults
        CreateRideDTO form = new CreateRideDTO("", "", LocalDateTime.now().plusHours(1), 3);
        model.addAttribute("createRideDTO", form);
        return "create-ride";
    }

    /**
     * Handle the form submission to create a ride.
     */
    @PostMapping("/rides/create")
    public String handleCreateRide(
            @Valid @ModelAttribute("createRideDTO") CreateRideDTO dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "create-ride"; // Return to form with validation errors
        }

        try {
            String username = currentUserProvider.getCurrentUser()
                    .orElseThrow(() -> new RuntimeException("Not authenticated"))
                    .username();

            rideService.createRide(dto, username);
            return "redirect:/rides?success=created"; // Redirect to dashboard

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // Business rule violation (e.g. limit reached)
            return "create-ride";
        }
    }

    /**
     * Show the current user's booked rides.
     */
    @GetMapping("/my-bookings")
    public String showMyBookings(Model model) {
        String username = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Not authenticated"))
                .username();

        // Note: You might want to create a BookingMapper later for strict safety,
        // but for now passing the entity is okay for the UI.
        var bookings = bookingService.getMyBookings(username);
        model.addAttribute("bookings", bookings);

        return "my-bookings";
    }

    /**
     * Show the rides offered by the current driver.
     */
    @GetMapping("/my-offered-rides")
    public String showMyOfferedRides(Model model) {
        String username = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Not authenticated"))
                .username();

        List<Ride> rides = rideService.getRidesByDriver(username);

        List<RideView> views = rides.stream()
                .map(rideMapper::toRideView)
                .collect(Collectors.toList());

        model.addAttribute("rides", views);

        return "my-offered-rides";
    }
}