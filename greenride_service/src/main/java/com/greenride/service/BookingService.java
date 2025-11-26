package com.greenride.service;

import com.greenride.model.Booking;
import com.greenride.model.Ride;
import com.greenride.model.User;
import com.greenride.repository.BookingRepository;
import com.greenride.repository.RideRepository;
import com.greenride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired private RideRepository rideRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public void bookRide(Long rideId, String passengerUsername) {
        // Fast fail: Check user existence
        User passenger = userRepository.findByUsername(passengerUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Fast fail: Check ride existence
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        // Rule: Driver cannot book their own ride
        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drivers cannot book their own ride.");
        }

        // Rule: Prevent duplicate bookings
        boolean alreadyBooked = ride.getBookings().stream()
                .anyMatch(b -> b.getPassenger().getId().equals(passenger.getId())
                        && !"CANCELLED".equals(b.getStatus()));
        if (alreadyBooked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already booked this ride.");
        }

        // ATOMIC DECREMENT: The core concurrency fix
        // If this returns 0, it means another thread took the last seat milliseconds ago.
        int updatedRows = rideRepository.decrementAvailableSeats(rideId);
        if (updatedRows == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride is fully booked!");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String username) {
        return bookingRepository.findByPassenger_Username(username);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        // Security check: Ensure the user cancelling is actually the one who booked
        if (!booking.getPassenger().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this booking.");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking is already cancelled.");
        }


        LocalDateTime departureTime = booking.getRide().getDepartureTime();
        long minutesUntilDeparture = Duration.between(LocalDateTime.now(), departureTime).toMinutes();

        if (minutesUntilDeparture < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel within 10 minutes of departure.");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Atomic Increment to restore seat
        rideRepository.incrementAvailableSeats(booking.getRide().getId());
    }
}