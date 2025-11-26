package com.greenride.repository;

import com.greenride.model.Booking;
import com.greenride.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassenger(User passenger);
    List<Booking> findByPassenger_Username(String username);
}