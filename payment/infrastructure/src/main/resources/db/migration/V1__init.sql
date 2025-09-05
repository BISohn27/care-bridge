CREATE TABLE users (
    id CHAR(36) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL ${updated_at_on_update}
) ${engine} ${charset};


CREATE TABLE cases (
    id CHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL,
    beneficiary_id CHAR(36) NOT NULL,
    target_amount BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL ${updated_at_on_update},
    FOREIGN KEY (beneficiary_id) REFERENCES users(id) ON DELETE CASCADE
) ${engine} ${charset};

CREATE TABLE case_status_audits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id CHAR(36) NULL, -- 당시 케이스 ID (FK 없음)
    beneficiary_id CHAR(36) NULL, -- 당시 수혜자 ID (FK 없음)

    previous_status VARCHAR(30) NULL, -- 최초 생성 이벤트는 NULL
    new_status VARCHAR(30) NOT NULL, -- 전이 결과 상태(항상 필요)

    actor_user_id CHAR(36) NULL, -- 누가 변경했는지(운영자/사용자/시스템)
    actor_role VARCHAR(16) NULL, -- ADMIN / USER / SYSTEM
    source VARCHAR(16) NULL, -- WEB / API / JOB / WEBHOOK / SYSTEM
    request_id VARCHAR(64) NULL, -- 요청/트랜잭션 상관관계 식별자(옵션)

    reason_code VARCHAR(50) NULL, -- 표준 사유 코드(예: DOC_INSUFFICIENT)
    reason_detail VARCHAR(500) NULL, -- 자유 서술(왜 그런 상태가 되었는지)

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ${engine} ${charset};


CREATE TABLE payments (
    id CHAR(36) NOT NULL PRIMARY KEY,
    case_id CHAR(36) NOT NULL,
    donor_id CHAR(36) DEFAULT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'KRW',
    status VARCHAR(20) NOT NULL,
    pg_provider_id BIGINT NOT NULL,
    pg_payment_id VARCHAR(100),
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    reason_code VARCHAR(50),
    reason_message VARCHAR(250),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (donor_id) REFERENCES users(id),
    FOREIGN KEY (case_id) REFERENCES cases(id),
    FOREIGN KEY (pg_provider_id) REFERENCES pg_providers(id),
    UNIQUE KEY uq_payments_pg (pg_provider_id, pg_payment_id)
) ${engine} ${charset};

ALTER TABLE payments ADD UNIQUE KEY uq_payments_pg (pg_provider, pg_payment_id);

CREATE TABLE payment_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id CHAR(36) NOT NULL,
    origin VARCHAR(20),
    event_id VARCHAR(100) NOT NULL UNIQUE,
    event_type VARCHAR(50) NOT NULL,
    raw_payload ${json_type} NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ${engine} ${charset};

CREATE TABLE pg_providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,      -- 시스템 아이디
    code VARCHAR(50) NOT NULL UNIQUE,          -- 예: 'toss', 'stripe', 'inicis'
    name VARCHAR(100) NOT NULL,                -- 표시용 이름
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / INACTIVE
    flow_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ${engine} ${charset};

CREATE TABLE pg_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_id BIGINT NOT NULL,               -- FK → pg_providers.id
    client_id VARCHAR(100) NOT NULL,
    api_key_encrypted VARCHAR(500) NOT NULL,   -- 암호화된 API 키
    environment VARCHAR(20) NOT NULL,          -- LIVE / TEST
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pg_accounts_provider FOREIGN KEY (provider_id) REFERENCES pg_providers(id)
) ${engine} ${charset};