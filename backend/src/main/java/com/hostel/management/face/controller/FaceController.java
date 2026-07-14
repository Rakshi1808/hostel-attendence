package com.hostel.management.face.controller;

import com.hostel.management.face.dto.RegisterFaceResponse;
import com.hostel.management.face.dto.VerifyFaceResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faces")
public class FaceController
{
    public  FaceController()
    {
    }

    @PostMapping("/register")
    public RegisterFaceResponse registerFace()
    {
        return null;
    }

    @PostMapping
    public VerifyFaceResponse verifyFace()
    {
    return null;
    }
}
