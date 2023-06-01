package com.example.apicall.dto;

import lombok.Data;

@Data
public class MomoPaymentResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private int amount;
    private long responseTime;
    private String message;
    private int resultCode;
    private String payUrl;

    // Constructors, getters, and setters
}
