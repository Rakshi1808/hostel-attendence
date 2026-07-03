package com.hostel.management.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateRequest
{
    @NotBlank
    private String rollNo;

    @NotBlank
    private String name;
    @NotBlank
    private String studentPhone;
    @NotNull
    @Positive
    private Integer yearOfStudy;

    private Integer programmeId;

}
