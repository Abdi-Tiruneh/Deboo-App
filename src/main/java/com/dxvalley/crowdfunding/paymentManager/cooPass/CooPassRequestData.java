package com.dxvalley.crowdfunding.paymentManager.cooPass;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CooPassRequestData {
    public String clientId;
    public String secrateKey;
    public String apiKey;
    public String callBackUrl;
    public String returnUrl;
    public String orderId;
    public String currency;
    public String phoneNumber;
    public String amount;
}

