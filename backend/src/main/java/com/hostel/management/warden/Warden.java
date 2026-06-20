package com.hostel.management.warden;

import com.hostel.management.hostel.Hostel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wardens")
public class Warden
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password;

    @JoinColumn(name = "hostel_id",nullable = false)
    @ManyToOne
    private Hostel hostel;

    @Column(name = "phone",nullable = false,unique = true)
    private String phoneNo;

    @Column(name="is_active",nullable = false)
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name="role",nullable = false)
    private WardenRole role;



}


