package com.greenride.service;

import com.greenride.dto.CreateRideDTO;
import com.greenride.model.Ride;
import com.greenride.model.User;
import com.greenride.repository.RideRepository;
import com.greenride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideServiceImpl implements RideService {

    private static final int MAX_ACTIVE_RIDES = 5; // Business Rule Constant

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Ride createRide(CreateRideDTO dto, String driverUsername) {
        User driver = userRepository.findByUsername(driverUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        // BUSINESS RULE: Limit active routes per driver
        long activeRides = rideRepository.countByDriver_UsernameAndDepartureTimeAfter(driverUsername, LocalDateTime.now());
        if (activeRides >= MAX_ACTIVE_RIDES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot have more than " + MAX_ACTIVE_RIDES + " active rides. Please complete or cancel existing ones.");
        }

        Ride ride = new Ride();
        ride.setStartLocation(dto.startLocation());
        ride.setDestination(dto.destination());
        ride.setDepartureTime(dto.departureTime());
        ride.setAvailableSeats(dto.availableSeats());
        ride.setDriver(driver);

        return rideRepository.save(ride);
    }

    @Override
    public List<Ride> searchRides(String start, String destination) {
        return rideRepository.findByStartLocationContainingIgnoreCaseAndDestinationContainingIgnoreCase(start, destination);
    }

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found with ID: " + rideId));
    }

    @Override
    public List<Ride> getRidesByDriver(String driverUsername) {
        return rideRepository.findByDriver_Username(driverUsername);
    }
}