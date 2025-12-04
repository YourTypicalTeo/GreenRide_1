package com.greenride.service;

import com.greenride.model.Booking;
import com.greenride.repository.BookingRepository;
import com.greenride.service.port.SmsNotificationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideReminderService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private SmsNotificationPort smsNotificationPort;

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendUpcomingRideReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            // Skip if cancelled
            if ("CANCELLED".equals(booking.getStatus())) {
                continue;
            }

            LocalDateTime departure = booking.getRide().getDepartureTime();

            // Logic:
            // 1. Is ride within the next hour?
            // 2. Have we NOT sent the reminder yet?
            if (departure.isAfter(now) &&
                    departure.isBefore(oneHourLater) &&
                    !booking.isReminderSent()) { // <--- CHECK THE FLAG

                System.out.println("⏰ REMINDER SENT: Ride for " + booking.getPassenger().getUsername());

                // Send the SMS
                smsNotificationPort.sendSms(booking.getPassenger().getPhoneNumber(),
                        "Reminder: Your ride to " + booking.getRide().getDestination() + " leaves in less than an hour!");

                // UPDATE STATE: Mark as sent so we don't spam
                booking.setReminderSent(true);
                bookingRepository.save(booking);
            }
        }
    }
}