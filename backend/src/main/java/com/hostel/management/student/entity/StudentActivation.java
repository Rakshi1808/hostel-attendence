package com.hostel.management.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="student_activations")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class StudentActivation
{
    @Id
    private String rollNo;

    @Column(name="activation_code",nullable = false)
    private String activationCode;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name="activated",nullable = false)
    private Boolean activated;

    @OneToOne
    @MapsId
    @JoinColumn(name = "roll_no")
    private Student student;
}
