package com.donation.carebridge.common.domain.idempotency.model;

public interface DuplicateCheckKeyed {

    String duplicateCheckKey();
}
