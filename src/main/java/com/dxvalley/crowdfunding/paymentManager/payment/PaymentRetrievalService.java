package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentResponse;

import java.util.List;

public interface PaymentRetrievalService {
    List<PaymentResponse> getPaymentByCampaignId(Long campaignId);
    List<PaymentResponse> myPayments();
}
