package com.greenride.service;

import com.greenride.model.Booking;
import com.greenride.repository.BookingRepository;
import com.greenride.service.port.SmsNotificationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideReminderService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private SmsNotificationPort smsNotificationPort;

    // Runs every 1 minute to check for rides starting soon
    @Scheduled(fixedRate = 60000)
    public void sendUpcomingRideReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // In a real app, you would query the DB efficiently.
        // For this demo, we fetch all and filter (inefficient but simple for small data).
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            LocalDateTime departure = booking.getRide().getDepartureTime();

            // Check if ride is between now and 1 hour from now
            if (departure.isAfter(now) && departure.isBefore(oneHourLater)) {
                // Send reminder logic (Mock)
                System.out.println("⏰ REMINDER: Ride for " + booking.getPassenger().getUsername() +
                        " leaves at " + departure);

                // Uncomment to actually SPAM the NOC server (be careful!)
                // smsNotificationPort.sendSms(booking.getPassenger().getPhoneNumber(),
                //    "Reminder: Your ride to " + booking.getRide().getDestination() + " leaves in less than an hour!");
            }
        }
    }
}