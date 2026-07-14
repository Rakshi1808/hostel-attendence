package com.hostel.management.face.client;

import com.hostel.management.face.dto.RegisterFaceResponse;
import com.hostel.management.face.dto.VerifyFaceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FlaskFaceClient implements FaceClient{

    private final RestClient restClient;
    @Value("${python.server.url}")
    private String pythonServerUrl;
    public FlaskFaceClient(RestClient restClient)
    {
        this.restClient = restClient;
    }
    @Override
    public RegisterFaceResponse registerFace(MultipartFile photo, String rollNo)
    {
        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();
        try{
            ByteArrayResource resource = new ByteArrayResource(photo.getBytes())
            {
                @Override
                public String getFilename() {
                    return photo.getOriginalFilename();
                }
            };
            body.add("rollNo", rollNo);
            body.add("photo", resource);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }


        return restClient
                .post()
                .uri(pythonServerUrl + "/register-face")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(RegisterFaceResponse.class);
    }

    @Override
    public VerifyFaceResponse verifyFace() {
        return null;
    }
}
