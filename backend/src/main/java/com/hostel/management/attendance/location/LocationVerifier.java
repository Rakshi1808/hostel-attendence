package com.hostel.management.attendance.location;


import com.hostel.management.attendance.dto.LocationVerificationResult;
import com.hostel.management.student.entity.Student;

public interface LocationVerifier {

    LocationVerificationResult verify(
            Student student,
            double latitude,
            double longitude
    );
}
