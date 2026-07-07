package com.hostel.management.student.dto;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.student.entity.Relation;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileResponse
{
    private String rollNo;

    private String name;

    private String personalEmail;

    private String studentPhone;

    private String programme;

    private Integer yearOfStudy;

    private String hostelName;

    private String roomNo;

    private String parentName;

    private String parentPhone;

    private Relation relation;

    private AccountStatus accountStatus;
}

