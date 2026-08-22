package com.hostel.management.face.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyFaceResponse {
    private boolean success;
    private String message;
    private float confidence;

}
