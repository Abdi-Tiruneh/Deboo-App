package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaInitResponse;
import com.dxvalley.crowdfunding.paymentManager.chapa.ChapaService;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassInitResponse;
import com.dxvalley.crowdfunding.paymentManager.cooPass.CooPassService;
import com.dxvalley.crowdfunding.paymentManager.ebirr.EbirrPaymentResponse;
import com.dxvalley.crowdfunding.paymentManager.ebirr.EbirrService;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentUpdateReq;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationServiceImpl implements PaymentOperationService {
    private final PaymentRepository paymentRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final ChapaService chapaService;
    private final EbirrService ebirrService;
    private final CooPassService cooPassService;
    private final PaymentUtils paymentUtils;

    @Override
    @Transactional
    public ResponseEntity processPaymentWithEbirr(PaymentRequest paymentRequest) {
        paymentUtils.validatePaymentPreconditions(paymentRequest.getCampaignId(), PaymentProcessor.EBIRR);
        Payment payment = paymentUtils.createPaymentFromPaymentReqDTO(paymentRequest, PaymentProcessor.EBIRR);
        PaymentRequest completePaymentRequest = completePaymentRequest(payment, paymentRequest);
        paymentRepository.save(payment);
        log.info("REQUEST TO EBIRR: {}", completePaymentRequest);
        EbirrPaymentResponse ebirrPaymentResponse = ebirrService.sendPaymentRequest(completePaymentRequest);
        updatePaymentStatusForEbirr(ebirrPaymentResponse, payment.getOrderId());
        log.info("RESPONSE FROM EBIRR: {}: {}", ebirrPaymentResponse);
        return ApiResponse.success("Payment completed successfully");
    }

    public void updatePaymentStatusForEbirr(EbirrPaymentResponse paymentFuture, String orderId) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setTransactionId(paymentFuture.getData().getTransactionId());
        paymentRepository.save(payment);
        paymentUtils.updateCampaignFromPayment(payment);
    }

    @Override
    @Transactional
    public ChapaInitResponse initializeChapaPayment(PaymentRequest paymentRequest) {
        paymentUtils.validatePaymentPreconditions(paymentRequest.getCampaignId(), PaymentProcessor.CHAPA);
        Payment payment = paymentUtils.createPaymentFromPaymentReqDTO(paymentRequest, PaymentProcessor.CHAPA);
        PaymentRequest completePaymentRequest = completePaymentRequest(payment, paymentRequest);
        paymentRepository.save(payment);
        log.info("REQUEST TO CHAPA: {}", completePaymentRequest);
        return chapaService.sendPaymentRequest(completePaymentRequest);
    }

    @Override
    @Transactional
    public CooPassInitResponse initializeCooPassPayment(PaymentRequest paymentRequest) {
        paymentUtils.validatePaymentPreconditions(paymentRequest.getCampaignId(), PaymentProcessor.COOPASS);
        Payment payment = paymentUtils.createPaymentFromPaymentReqDTO(paymentRequest, PaymentProcessor.COOPASS);
        PaymentRequest completePaymentRequest = completePaymentRequest(payment, paymentRequest);
        paymentRepository.save(payment);
        log.info("REQUEST TO COOPASS: {}", completePaymentRequest);
        return cooPassService.sendPaymentRequest(completePaymentRequest);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> updateStatus(String orderId, PaymentUpdateReq paymentUpdateReq) {
        Payment payment = paymentUtils.getPaymentByOrderId(orderId);
        payment.setTransactionId(paymentUpdateReq.getTransactionId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionCompletedDate(LocalDateTime.now().format(dateTimeFormatter));
        paymentUtils.updateCampaignFromPayment(payment);
        return ApiResponse.success("Transaction completed successfully");
    }

    private PaymentRequest completePaymentRequest(Payment payment, PaymentRequest paymentReqDTOReq) {
        paymentReqDTOReq.setOrderId(payment.getOrderId());
        return paymentReqDTOReq;
    }
}
