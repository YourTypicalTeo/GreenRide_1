package com.greenride.service.mapper;

import com.greenride.dto.RideView;
import com.greenride.dto.UserView;
import com.greenride.model.Ride;
import com.greenride.model.User;
import org.springframework.stereotype.Component;

@Component
public class RideMapper {

    public RideView toRideView(Ride ride) {
        if (ride == null) {
            return null;
        }

        User driver = ride.getDriver();
        UserView driverView = null;

        if (driver != null) {
            driverView = new UserView(
                    driver.getId(),
                    driver.getUsername(),
                    driver.getEmail()
            );
        }

        return new RideView(
                ride.getId(),
                ride.getStartLocation(),
                ride.getDestination(),
                ride.getDepartureTime(),
                ride.getAvailableSeats(),
                driverView
        );
    }
}