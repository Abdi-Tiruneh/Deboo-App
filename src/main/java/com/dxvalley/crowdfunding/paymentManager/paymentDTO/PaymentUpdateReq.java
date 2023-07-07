package com.dxvalley.crowdfunding.paymentManager.paymentDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentUpdateReq {
    @NotBlank(message = "transactionId cannot be blank")
    private String transactionId;
}
