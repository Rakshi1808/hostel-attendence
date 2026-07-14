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
    String rollNo;
    String message;
    ArrayList<Float> embedding;
}