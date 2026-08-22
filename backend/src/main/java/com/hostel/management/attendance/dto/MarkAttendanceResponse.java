package com.hostel.management.attendance.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkAttendanceResponse {

    private boolean success;
    private String message;
}
