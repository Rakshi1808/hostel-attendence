package com.hostel.management.attendance.controller;

import com.hostel.management.attendance.dto.AttendanceStatusResponse;
import com.hostel.management.attendance.dto.MarkAttendanceResponse;
import com.hostel.management.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/status")
    public ResponseEntity<AttendanceStatusResponse> getAttendanceStatus() {
        return ResponseEntity.ok(
                attendanceService.checkAttendanceEligibility()
        );
    }


    @PostMapping("/mark")
    public ResponseEntity<MarkAttendanceResponse> markAttendance(
            @RequestPart("photo") MultipartFile photo,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        return ResponseEntity.ok(
                attendanceService.markAttendance(
                        photo, latitude, longitude
                )
        );
    }
}
