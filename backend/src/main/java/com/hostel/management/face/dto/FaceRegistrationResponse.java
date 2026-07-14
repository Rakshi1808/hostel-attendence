package com.hostel.management.face.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FaceRegistrationResponse {
    String message;
    boolean success;
}
