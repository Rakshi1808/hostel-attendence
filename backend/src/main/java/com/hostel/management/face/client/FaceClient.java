package com.hostel.management.face.client;

import com.hostel.management.face.dto.RegisterFaceResponse;
import com.hostel.management.face.dto.VerifyFaceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FaceClient {
    RegisterFaceResponse registerFace(MultipartFile photo, String rollNo);

    VerifyFaceResponse verifyFace(
            MultipartFile photo,
            String rollNo,
            String embedding
    );

}
