package com.hostel.management.student.dto;

import lombok.*;


import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivationGenerateResponse
{
    private String rollNo;
    private LocalDateTime expiresAt;
    private String activationCode;
}
