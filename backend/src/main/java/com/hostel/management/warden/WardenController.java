package com.hostel.management.warden;

import com.hostel.management.common.response.ApiResponse;
import com.hostel.management.warden.dto.PendingFaceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warden")
public class WardenController {
    private WardenService wardenService;

    WardenController(WardenService wardenService) {
        this.wardenService = wardenService;
    }

    @GetMapping("/faces/peding")
    public List<PendingFaceResponse> pendingFaceResponse()
    {
        return wardenService.pendingFaceResponse();
    }

    @PutMapping("/faces/{rollNo}/approve")
    public ResponseEntity<ApiResponse> approveFace(@PathVariable String rollNo)
    {
        return ResponseEntity.ok(wardenService.approveFace(rollNo));
    }

    @PutMapping("/faces/{rollNo}/reject")
    public ResponseEntity<ApiResponse> rejectFace(@PathVariable String rollNo)
    {
        return ResponseEntity.ok(wardenService.rejectFace(rollNo));
    }


}
