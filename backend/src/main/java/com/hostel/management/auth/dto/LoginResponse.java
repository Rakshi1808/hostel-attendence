package com.hostel.management.auth.dto;

import com.hostel.management.common.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse
{
    private String token;

    private Role role;
    @NotBlank
    private String message;
}
