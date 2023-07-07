package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaInitResponse;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentUpdateReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentOperationService {
    ResponseEntity<ApiResponse> processPaymentWithEbirr(PaymentRequest paymentRequest);

    ChapaInitResponse initializeChapaPayment(PaymentRequest paymentRequest);

    CooPassInitResponse initializeCooPassPayment(PaymentRequest requestDTO);

    ResponseEntity<ApiResponse> updateStatus(String orderId, PaymentUpdateReq paymentUpdateReq);
}