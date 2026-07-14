package com.hostel.management.face.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyFaceResponse {
    boolean success;
    String message;
    float confidence;
    boolean matched;

}
