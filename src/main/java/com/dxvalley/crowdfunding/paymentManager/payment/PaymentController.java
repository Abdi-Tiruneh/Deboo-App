package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaInitResponse;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentUpdateReq;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment API", description = "API endpoints for making and retrieving payment information")
public class PaymentController {
    private final PaymentRetrievalService paymentRetrievalService;
    private final PaymentOperationService paymentOperationService;

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> myPayments() {
        return ResponseEntity.ok(paymentRetrievalService.myPayments());
    }

    @GetMapping("/byCampaign/{campaignId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(paymentRetrievalService.getPaymentByCampaignId(campaignId));
    }

    @PostMapping("/payWithEbirr")
    public ResponseEntity<ApiResponse> payWithEbirr(@RequestBody @Valid PaymentRequest paymentRequest) {
        return paymentOperationService.processPaymentWithEbirr(paymentRequest);
    }

    @PostMapping("/payWithChapa")
    public ResponseEntity<ChapaInitResponse> payWithChapa(@RequestBody @Valid PaymentRequest chapaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentOperationService.initializeChapaPayment(chapaRequest));
    }

    @PostMapping("/payWithCooPass")
    public ResponseEntity<CooPassInitResponse> payWithCooPass(@RequestBody @Valid PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentOperationService.initializeCooPassPayment(paymentRequest));
    }

    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable String orderId, @RequestBody @Valid PaymentUpdateReq paymentUpdateReq) {
        return paymentOperationService.updateStatus(orderId, paymentUpdateReq);
    }

    public PaymentController(PaymentRetrievalService paymentRetrievalService, PaymentOperationService paymentOperationService) {
        this.paymentRetrievalService = paymentRetrievalService;
        this.paymentOperationService = paymentOperationService;
    }

}
