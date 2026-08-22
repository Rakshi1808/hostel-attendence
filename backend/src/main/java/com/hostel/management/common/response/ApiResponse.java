package com.hostel.management.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiResponse
{
    String message;
    boolean success;
}
