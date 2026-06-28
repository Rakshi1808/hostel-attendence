package com.hostel.management.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentActivationRequest
{
    @NotBlank
    String rollNo;
    @NotBlank
    String personalEmail;
    @NotBlank
    String password;
    @NotBlank
    String activationCode;
}
