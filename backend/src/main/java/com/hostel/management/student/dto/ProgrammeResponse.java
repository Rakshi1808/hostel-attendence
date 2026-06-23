package com.hostel.management.student.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammeResponse
{
    private Integer id;

    private String programmeName;

    private Integer totalYears;

    private Boolean isActive;
}
