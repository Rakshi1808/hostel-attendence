package com.hostel.management.hostel.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "programmes")
public class Programme
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "programme_name" , nullable = false)
    private String programmeName;

    @Column(name = "total_years", nullable = false)
    private Integer totalYears;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
