package com.donation.carebridge.donation.domain.payment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Origin {

    SYSTEM("SYS"), PG("PG"), USER("USR");

    private final String code;
}
