package com.donation.carebridge.donation.domain.donationcase.application.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DonationCaseStatus {

    // 초기 단계
    DRAFT("수혜자가 사연을 작성만 하고 아직 제출하지 않은 상태"),
    UNDER_REVIEW("소득 증명 업로드 후, 관리자가 승인/반려를 기다리는 상태"),

    // 모금 단계
    FUNDING("관리자가 승인한 케이스로 후원자 피드에 공개되어 결제 가능한 상태"),
    EXTENDED("목표 금액이나 기한 연장을 통해 모금을 이어가는 상태"),

    // 지급 단계
    AWAITING_PAYOUT("모금이 마감되고, 영수증 검수 완료 후 실제 지급을 기다리는 상태"),
    PAID("가상 지급 또는 실제 지급까지 끝난 상태"),

    // 종료 및 예외
    CLOSED("모금 종료 및 지급 처리 완료로 후원자 열람만 가능한 상태"),
    REJECTED("소득 증명 위조/불충분으로 승인 불가"),
    CANCELLED("진행 중 문제가 발견되어 모금 중단 및 환불 프로세스 진행");

    private final String description;
}
