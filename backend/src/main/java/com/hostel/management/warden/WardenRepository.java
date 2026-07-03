package com.hostel.management.warden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WardenRepository extends JpaRepository<Warden,Integer> {
    boolean existsByEmail(String email);
    Optional<Warden> findByEmail(String email);
}
