package com.hostel.management.student.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentContact
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "roll_no",nullable = false,unique = true)
    private Student student;

    @Column(name = "parent_name",nullable = false)
    private String parentName;

    @Column(name = "parent_Phone",nullable = false)
    private String parentPhone;

    @Enumerated(EnumType.STRING)
    @Column(name="relation",nullable = false)
    private Relation relation;

}
