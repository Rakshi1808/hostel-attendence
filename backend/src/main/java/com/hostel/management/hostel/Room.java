package com.hostel.management.hostel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name= "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_no",nullable = false)
    private String roomNo;

    @Column(name = "floor_no")
    private  Integer floorNo;

    @Column(name = "block")
    private String block;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "capacity",nullable = false)
    private Integer capacity;

    @JoinColumn(name = "hostel_id",nullable = false)
    @ManyToOne
    private Hostel hostel;


}
