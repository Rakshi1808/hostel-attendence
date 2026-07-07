package com.hostel.management.hostel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Integer> {
    Optional<Room> findByHostelIdAndRoomNoAndIsActiveTrue(Integer hostelId, String roomNo);
}
