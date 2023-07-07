package com.dxvalley.crowdfunding.paymentManager.chapa;

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
@RequiredArgsConstructor
@Slf4j
public class ChapaService {
    private final ChapaProperties chapaProperties;
    private final RestTemplate restTemplate;
    private final PaymentUtils paymentUtils;
    @Value("${appUrl.paymentCallBack}")
    private String callBack;

    public ChapaRequestData createRequestData(PaymentRequest paymentRequest) {
        return ChapaRequestData.builder()
                .clientId(chapaProperties.getClientId())
                .secrateKey(chapaProperties.getSecrateKey())
                .apiKey(chapaProperties.getApiKey())
                .callBackUrl(callBack)
                .returnUrl(paymentRequest.getReturnUrl())
                .email(paymentRequest.getContact())
                .first_name(paymentRequest.getFirstName() !=null ? paymentRequest.getFirstName() : "Anonymous")
                .last_name(paymentRequest.getLastName() !=null ? paymentRequest.getLastName() : "Anonymous")
                .tx_ref(paymentRequest.getOrderId())
                .title("Deboo App").description("Deboo App")
                .currency("ETB")
                .authToken(chapaProperties.getAuthToken())
                .amount(String.valueOf(paymentRequest.getAmount()))
                .build();
    }


    public ChapaInitResponse sendPaymentRequest(PaymentRequest paymentRequest) {
        ChapaRequestData chapaRequestData = createRequestData(paymentRequest);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ChapaRequestData> request = new HttpEntity(chapaRequestData, headers);
            ResponseEntity<ChapaInitResponse> paymentResponse = restTemplate.postForEntity(chapaProperties.getUrl(), request, ChapaInitResponse.class);

            if (paymentResponse.getStatusCode().is2xxSuccessful())
                return paymentResponse.getBody();
            else
                throw new PaymentCannotProcessedException("Error processing payment");

        } catch (Exception ex) {
            log.error("Error message: {}. payment request {}.",
                    ex.getMessage(),chapaRequestData);
            paymentUtils.handleFailedPayment(paymentRequest.getOrderId());
            throw new PaymentCannotProcessedException("Currently, we can't process payments with Chapa");
        }
    }
}

@Component
@ConfigurationProperties(prefix = "chapa")
@Getter
@Setter
class ChapaProperties {
    private String clientId;
    private String secrateKey;
    private String apiKey;
    private String url;
    private String authToken;

}

