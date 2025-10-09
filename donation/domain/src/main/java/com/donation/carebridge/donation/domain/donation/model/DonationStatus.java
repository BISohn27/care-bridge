package com.donation.carebridge.donation.domain.donation.model;

import lombok.Getter;

@Getter
public enum DonationStatus {

    PENDING("후원금 결제 대기 중"),
    COMPLETED("후원금 결제 완료, 후원 확정"),
    CANCELLED("사용자 취소"),
    EXPIRED("시간 초과로 만료");

    private final String description;

    DonationStatus(String description) {
        this.description = description;
    }
}
