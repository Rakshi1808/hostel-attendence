package com.hostel.management.attendance.entity;


import com.hostel.management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "roll_no", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private AttendanceSession session;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "marked_at", updatable = false)
    private LocalDateTime markedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "marked_by", nullable = false)
    private MarkedBy markedBy;
}