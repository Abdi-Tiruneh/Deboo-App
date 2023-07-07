package com.dxvalley.crowdfunding.paymentManager.cooPass;

import com.dxvalley.crowdfunding.exception.customException.PaymentCannotProcessedException;
import com.dxvalley.crowdfunding.paymentManager.payment.PaymentUtils;
import com.dxvalley.crowdfunding.paymentManager.paymentDTO.PaymentRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class CooPassService {
    private final CooPassProperties cooPassProperties;
    private final RestTemplate restTemplate;
    private final PaymentUtils paymentUtils;
    @Value("${appUrl.paymentCallBack}")
    private String callBack;

    public CooPassRequestData createRequestData(PaymentRequest paymentRequest) {
        return CooPassRequestData.builder()
                .secrateKey(cooPassProperties.getSecrateKey())
                .clientId(cooPassProperties.getClientId())
                .apiKey(cooPassProperties.getApiKey())
                .callBackUrl(callBack)
                .returnUrl(paymentRequest.getReturnUrl())
                .orderId(paymentRequest.getOrderId())
                .currency("ETB")
                .phoneNumber(paymentRequest.getContact())
                .amount(String.valueOf(paymentRequest.getAmount()))
                .build();
    }

    public CooPassInitResponse sendPaymentRequest(PaymentRequest paymentRequest) {
        CooPassRequestData cooPassRequestData = createRequestData(paymentRequest);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CooPassRequestData> request = new HttpEntity(cooPassRequestData, headers);
            ResponseEntity<CooPassInitResponse> paymentResponse = restTemplate.postForEntity(cooPassProperties.getUrl(), request, CooPassInitResponse.class);

            if (paymentResponse.getStatusCode().is2xxSuccessful()) {
                return paymentResponse.getBody();
            } else {
                throw new PaymentCannotProcessedException("Error processing payment");
            }
        } catch (Exception ex) {
            log.error("Error message: {}. payment request {}.",
                     ex.getMessage(),cooPassRequestData);
            paymentUtils.handleFailedPayment(paymentRequest.getOrderId());
            throw new PaymentCannotProcessedException("Currently, we can't process payments with Coopass");
        }
    }

}

@Component
@ConfigurationProperties(prefix = "coopass")
@Getter
@Setter
class CooPassProperties {
    private String clientId;
    private String secrateKey;
    private String apiKey;
    private String url;
}
