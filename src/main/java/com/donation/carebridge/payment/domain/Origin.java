package com.donation.carebridge.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Origin {

    SYSTEM("SYS"), PG("PG"), USER("USR");

    private final String code;
}
