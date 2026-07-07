package com.hostel.management.student.dto;

import com.hostel.management.student.entity.Relation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileRequest
{
    @NotNull
    private Integer hostelId;

    @NotBlank
    private String roomNo;

    @NotNull
    private Relation relation;

    @NotBlank
    private String parentName;

    @NotBlank
    private String parentPhone;
}
