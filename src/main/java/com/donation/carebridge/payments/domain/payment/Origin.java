package com.donation.carebridge.payments.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Origin {

    SYSTEM("SYS"), PG("PG"), USER("USR");

    private final String code;
}
