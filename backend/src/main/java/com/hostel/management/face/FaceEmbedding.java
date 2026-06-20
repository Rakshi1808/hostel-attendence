package com.hostel.management.face;

import com.hostel.management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "face_embeddings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceEmbedding
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name= "roll_no", nullable = false)
    private Student student;

    @Column(name = "photo_path",nullable = false)
    private String photoPath;

    @Column(name = "embedding",nullable = false,columnDefinition = "JASON")
    private String embedding;

    @Column(name = "is_active",nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "registered_by",nullable = false)
    private RegisteredBy registeredBy;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

}
