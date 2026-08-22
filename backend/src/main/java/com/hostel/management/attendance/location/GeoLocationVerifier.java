package com.hostel.management.attendance.location;

import com.hostel.management.attendance.dto.LocationVerificationResult;
import com.hostel.management.hostel.Hostel;
import com.hostel.management.student.entity.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationVerifier implements LocationVerifier {

    @Value("${attendance.location.radius-meters}")
    private double allowedRadius;

    @Override
    public LocationVerificationResult verify(
            Student student,
            double latitude,
            double longitude) {

        // Validate latitude
        if (latitude < -90 || latitude > 90) {
            return LocationVerificationResult.builder()
                    .valid(false)
                    .distance(0)
                    .message("Invalid latitude")
                    .build();
        }

        // Validate longitude
        if (longitude < -180 || longitude > 180) {
            return LocationVerificationResult.builder()
                    .valid(false)
                    .distance(0)
                    .message("Invalid longitude")
                    .build();
        }

        Hostel hostel = student.getRoom().getHostel();

        double distance = calculateDistance(
                latitude,
                longitude,
                hostel.getLatitude(),
                hostel.getLongitude()
        );

        boolean valid = distance < allowedRadius;

        return LocationVerificationResult.builder()
                .valid(valid)
                .distance(distance)
                .message(valid
                        ? "Student is within hostel location"
                        : "Student is outside hostel location")
                .build();
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS = 6371000;

        double latDistance =
                Math.toRadians(lat2 - lat1) * EARTH_RADIUS;

        double averageLatitude =
                Math.toRadians((lat1 + lat2) / 2);

        double lonDistance =
                Math.toRadians(lon2 - lon1)
                        * EARTH_RADIUS
                        * Math.cos(averageLatitude);

        return Math.sqrt(
                latDistance * latDistance
                        + lonDistance * lonDistance
        );
    }
}