package com.greenride.service;

import com.greenride.dto.CreateRideDTO;
import com.greenride.model.Ride;
import java.util.List;

public interface RideService {
    Ride createRide(CreateRideDTO createRideDTO, String driverUsername);
    List<Ride> searchRides(String start, String destination);
    Ride getRideById(Long rideId);
    List<Ride> getRidesByDriver(String driverUsername);
}