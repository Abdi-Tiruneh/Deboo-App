package com.dxvalley.crowdfunding.paymentManager.payment;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGatewayService;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentUtils {
    private final PaymentRepository paymentRepository;
    private final CampaignRepository campaignRepository;
    private final UserUtils userUtils;
    private final PaymentGatewayService paymentGatewayService;
    private final DateTimeFormatter dateTimeFormatter;

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findPaymentByOrderId(orderId).orElseThrow(() -> {
            throw new ResourceNotFoundException("Payment not found");
        });
    }

    public Campaign getCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId).orElseThrow(() -> {
            return new ResourceNotFoundException("Campaign not found");
        });
    }

    public Users getUserById(Long userId) {
        return userId != null ? userUtils.utilGetUserByUserId(userId) : null;
    }

    //Validates the preconditions for payment processing.
    public void validatePaymentPreconditions(Long campaignId, PaymentProcessor paymentProcessor) {
        boolean isActive = paymentGatewayService.isPaymentGatewayActive(paymentProcessor.name());
        if (!isActive)
            throw new ForbiddenException("The payment gateway is currently unavailable. Please try again later.");

        Campaign campaign = getCampaignById(campaignId);
        if (campaign.getCampaignStage() != CampaignStage.FUNDING)
            throw new ForbiddenException("This campaign is not accepting payments at the moment. Please check back later.");
    }


    public Payment createPaymentFromPaymentReqDTO(PaymentRequest paymentRequest, PaymentProcessor paymentProcessor) {
        Campaign campaign = getCampaignById(paymentRequest.getCampaignId());
        Users user = getUserById(paymentRequest.getUserId());
        String orderId = generateUniqueOrderId(campaign.getFundingType().getName());

        Payment payment = new Payment();
        payment.setPayerFullName(paymentRequest.getFirstName() + " " + paymentRequest.getLastName());
        payment.setPaymentContactInfo(paymentRequest.getContact());
        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
        payment.setIsAnonymous(paymentRequest.isAnonymous());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency("ETB");
        payment.setPaymentProcessor(paymentProcessor);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setCampaign(campaign);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return payment;
    }


    public void handleFailedPayment(String orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Transactional
    public double updateCampaignFromPayment(Payment payment) {
        Campaign campaign = payment.getCampaign();
        List<Payment> payments = paymentRepository.findByCampaignIdAndPaymentStatus(campaign.getId(), PaymentStatus.SUCCESS);
        if (payments.isEmpty()) {
            return 0.0;
        } else {
            double totalAmountCollected = payments.stream().mapToDouble((pay) -> {
                return pay.getCurrency().equals("USD") ? pay.getAmount() * 54.0 : pay.getAmount();
            }).sum();
            campaign.setNumberOfBackers(campaign.getNumberOfBackers() + 1);
            campaign.setTotalAmountCollected(totalAmountCollected);
            campaignRepository.save(campaign);
            return totalAmountCollected;
        }
    }

    // Generates a unique order ID based on the funding type.
    public String generateUniqueOrderId(String fundingType) {
        final String ALL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        final int LENGTH = 17;
        SecureRandom random = new SecureRandom();

        String orderId;
        do {
            StringBuilder stringBuilder = new StringBuilder(LENGTH);
            for (int i = 0; i < LENGTH; i++) {
                int randomIndex = random.nextInt(ALL_CHARS.length());
                stringBuilder.append(ALL_CHARS.charAt(randomIndex));
            }
            String randomString = stringBuilder.toString();

            orderId = switch (fundingType.toUpperCase()) {
                case "DONATION" -> "DN_" + randomString;
                case "EQUITY" -> "EQ_" + randomString;
                case "REWARD" -> "RW_" + randomString;
                default -> randomString;
            };
        } while (paymentRepository.findPaymentByOrderId(orderId).isPresent());

        return orderId;
    }


}
