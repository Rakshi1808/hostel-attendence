package com.hostel.management.student.dto;

import com.hostel.management.common.enums.AccountStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryResponse
{
    private String rollNo;

    private String name;

    private String programmeName;

    private Integer yearOfStudy;

    private String roomNumber;

    private AccountStatus accountStatus;
}
