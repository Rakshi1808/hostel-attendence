package com.hostel.management.student.entity;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.hostel.Room;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student
{
    @Id
    @Column(name = "roll_no",nullable = false)
    private String rollNo;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @Column(name = "password",nullable = false,unique = true)
    private String password;

    @Column(name = "student_phone",nullable = false,unique = true)
    private String studentPhone;

    @Column(name = "year_of_study",nullable = false)
    private Integer yearOfStudy;

    @ManyToOne
    @JoinColumn(name ="programme_id",nullable = false)
    private Programme programme;

    @ManyToOne
    @JoinColumn(name="room_id",nullable = false)
    private Room room;


    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

}
