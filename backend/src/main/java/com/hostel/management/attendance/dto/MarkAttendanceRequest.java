package com.hostel.management.attendance.dto;

import org.springframework.web.multipart.MultipartFile;

public class MarkAttendanceRequest {

    private MultipartFile photo;

    private Double latitude;

    private Double longitude;
}
