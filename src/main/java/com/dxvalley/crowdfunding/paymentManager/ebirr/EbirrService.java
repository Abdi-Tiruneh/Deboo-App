package com.dxvalley.crowdfunding.paymentManager.ebirr;

import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.paymentManager.payment.PaymentUtils;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EbirrService {
    private final RestTemplate restTemplate;
    private final EbirrProperties ebirrProperties;
    private final PaymentUtils paymentUtils;

    public RequestData createRequestData(PaymentRequest paymentRequest) {
        return RequestData.builder()
                .clientId(ebirrProperties.getClientId())
                .secrateKey(ebirrProperties.getSecrateKey())
                .apiKey(ebirrProperties.getApiKey())
                .orderID(paymentRequest.getOrderId())
                .requestId(paymentRequest.getOrderId())
                .referenceId(paymentRequest.getOrderId())
                .invoiceId(paymentRequest.getOrderId())
                .accountNo(paymentRequest.getContact())
                .amount(String.valueOf(paymentRequest.getAmount()))
                .build();
    }

    public EbirrPaymentResponse sendPaymentRequest(PaymentRequest paymentRequest) {
        RequestData ebirrPaymentRequest = createRequestData(paymentRequest);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RequestData> request = new HttpEntity(ebirrPaymentRequest, headers);
            ResponseEntity<EbirrPaymentResponse> paymentResponse = restTemplate.postForEntity(ebirrProperties.getUrl(), request, EbirrPaymentResponse.class);
            if (paymentResponse.getStatusCode().is2xxSuccessful())
                return paymentResponse.getBody();
            else
                throw new PaymentCannotProcessedException("Error processing payment");

        } catch (Exception ex) {
            log.error("Error message: {}. payment request {}.",
                    ex.getMessage(), ebirrPaymentRequest);
            paymentUtils.handleFailedPayment(paymentRequest.getOrderId());
            throw new PaymentCannotProcessedException("Currently, we can't process payments with Ebirr");
        }
    }
}

@Component
@ConfigurationProperties(prefix = "ebirr")
@Getter
@Setter
class EbirrProperties {
    private String clientId;
    private String secrateKey;
    private String apiKey;
    private String url;
}

