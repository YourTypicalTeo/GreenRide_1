package com.greenride.repository;

import com.greenride.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByStartLocationContainingIgnoreCaseAndDestinationContainingIgnoreCase(String startLocation, String destination);

    List<Ride> findByDriver_Username(String username);

    // NEW: Count active rides for a specific driver
    long countByDriver_UsernameAndDepartureTimeAfter(String username, LocalDateTime now);

    // ATOMIC UPDATE: Decrements seats
    @Transactional
    @Modifying
    @Query("UPDATE Ride r SET r.availableSeats = r.availableSeats - 1 WHERE r.id = :id AND r.availableSeats > 0")
    int decrementAvailableSeats(@Param("id") Long id);

    // ATOMIC UPDATE: Increments seats
    @Transactional
    @Modifying
    @Query("UPDATE Ride r SET r.availableSeats = r.availableSeats + 1 WHERE r.id = :id")
    void incrementAvailableSeats(@Param("id") Long id);
}