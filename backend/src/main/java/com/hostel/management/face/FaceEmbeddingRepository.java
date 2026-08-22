package com.hostel.management.face;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaceEmbeddingRepository extends JpaRepository<FaceEmbedding,Integer> {
    Optional<FaceEmbedding> findByStudentRollNoAndIsActiveTrue(String studentRollNo);
    boolean existsByStudentRollNoAndIsActiveTrue(String studentRollNo);
}
