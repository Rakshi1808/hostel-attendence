package com.hostel.management.student.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammeCreateRequest
{
    @NotBlank
    private String programmeName;

    @NotNull
    @Positive
    private Integer totalYears;
}
