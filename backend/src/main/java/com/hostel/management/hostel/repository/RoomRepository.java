package com.hostel.management.hostel.repository;

import com.hostel.management.hostel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Integer> {
}
