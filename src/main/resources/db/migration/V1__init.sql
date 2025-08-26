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
    pg_provider VARCHAR(50),
    pg_payment_id VARCHAR(100),
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL ${updated_at_on_update},
    paid_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (donor_id) REFERENCES users(id),
    FOREIGN KEY (case_id) REFERENCES cases(id)
) ${engine} ${charset};

CREATE TABLE payment_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id CHAR(36) NOT NULL,
    event_id VARCHAR(100) NOT NULL UNIQUE,
    event_type VARCHAR(50) NOT NULL,
    raw_payload ${json_type} NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ${engine} ${charset};