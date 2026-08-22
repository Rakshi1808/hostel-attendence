package com.hostel.management.face.dto;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterFaceResponse {
    boolean success;
    private String photoPath;
    String message;
    ArrayList<Float> embedding;
}
