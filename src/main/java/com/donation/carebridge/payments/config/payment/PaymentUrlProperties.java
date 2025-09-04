package com.donation.carebridge.payments.config.payment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PaymentUrlProperties {

    @Value("${app.payment.url.success}")
    private String successUrl;

    @Value("${app.payment.url.fail}")
    private String failUrl;

    @Value("${app.payment.url.cancel}")
    private String cancelUrl;
}