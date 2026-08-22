package com.hostel.management.warden.dto;

import com.hostel.management.common.enums.AccountStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PendingFaceResponse
{
    String rollNo;
    String name;
    String photoPath;
    LocalDateTime registeredAt;
}
