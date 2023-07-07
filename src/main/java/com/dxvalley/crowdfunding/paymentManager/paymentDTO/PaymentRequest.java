package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long userId;
    private @NotNull(message = "Campaign ID cannot be null")
    Long campaignId;
    private @NotNull(message = "Amount cannot be null")
    Double amount;
    private @NotBlank(message = "contact cannot be blank")
    String contact;
    String firstName;
    String lastName;
    String returnUrl;
    private boolean isAnonymous;
    private String orderId;
}