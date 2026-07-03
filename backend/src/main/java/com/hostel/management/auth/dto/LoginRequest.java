package com.hostel.management.auth.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest
{
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
