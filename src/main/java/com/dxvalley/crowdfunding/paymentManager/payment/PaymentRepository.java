package com.dxvalley.crowdfunding.paymentManager.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByOrderId(String orderId);

    List<Payment> findByCampaignIdAndPaymentStatus(Long campaignId, PaymentStatus paymentStatus);

    List<Payment> findByUserUsernameAndPaymentStatus(String username,PaymentStatus paymentStatus);

    List<Payment> findByCampaignId(Long campaignId);
}
