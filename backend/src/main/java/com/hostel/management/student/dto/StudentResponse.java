package com.hostel.management.student.dto;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.hostel.Room;
import com.hostel.management.student.entity.Programme;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse
{
    private String rollNo;

    private String name;

    private String personalEmail;

    private String studentPhone;

    private Integer yearOfStudy;

    private Integer programmeId;

    private String programmeName;

    private Integer programmeTotalYears;

    private Integer roomId;

    private String roomNumber;

    private AccountStatus accountStatus;
}