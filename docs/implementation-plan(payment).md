1. # 결제 기본 설계서 (요청 → 승인, MVP)

   > 목적: 여러 PG사의 차이를 흡수하면서 **요청 → 승인(Confirm)** 까지 일관된 도메인 흐름을 제공한다.
   > (취소/환불은 후속 단계에서 다룸)

   ------

   ## 1. 범위 및 원칙

   - **범위**
     - 결제 생성(Create) → (필요 시) 사용자 인증(클라 SDK/Redirect) → 승인(Confirm) → 성공/실패 반환
   - **원칙**
     - 상태머신은 도메인 소유
     - 계약 우선(Contract-first)
     - 멱등성 보장(Create 키 + Confirm 키)
     - HY 전략(코어 표준 필드 + providerPayload 원형 패스스루)

   ------

   ## 2. 용어와 상태

   - **PG**: 결제대행사
   - **providerPayload**: PG별 원본 데이터(보존). 우리 시스템에서는 `payment_events.raw_payload` 컬럼에 저장
   - **paymentId (**``***\**\**\**\*)**: 내부 결제 식별자, donation/order와 동일한 역할
   - **pgPaymentId**: PG사가 발급한 결제 식별자 (예: 토스의 paymentKey)
   - **외부 노출 상태**: `CREATED → REQUIRES_ACTION → SUCCESS | FAILED`
   - **내부 상태**: EXECUTING (confirm 처리 중, 영속화 X)
   - **전이 규칙**
     - `CREATED → REQUIRES_ACTION`: create 성공 후 클라이언트 액션 필요할 때
     - `REQUIRES_ACTION → (``EXECUTING``) → SUCCESS | FAILED`: confirm 결과에 따라 전이
     - 금지: `FAILED → SUCCESS`, `CREATED → SUCCESS|FAILED` 직접 전이

   ------

   ## 3. API 계약

   ### 3.1 Create

   **POST /payments**

   요청(JSON)

   ```json
   {
     "caseId": "c-001",
     "donorId": "u-123",
     "amount": 12000,
     "currency": "KRW",
     "method": "CARD",
     "idempotencyKey": "idem-123"
     // pgProviderCode는 optional (없으면 서버 라우팅)
   }
   ```

   응답(JSON)

   ```json
   {
     "paymentId": "pay-123", // 우리 시스템의 payments.id (donation/order id). 타 시스템에서는 order와 payment를 별도 테이블로 나누는 경우가 많아 orderId와 paymentId가 다를 수 있으나, 본 시스템에서는 donation=order=payment 개념을 통합해 사용
     "status": "REQUIRES_ACTION",
     "nextAction": {
       "type": "CLIENT_SDK | REDIRECT | SERVER_ONLY | NONE",
       "provider": "toss",
       "payload": {
         "clientKey": "pk_live_xxx",
         "amount": 12000,
         "currency": "KRW",
         "successUrl": "https://app.example/success?pid=pay-123",
         "failUrl": "https://app.example/fail?pid=pay-123"
       }
     }
   }
   ```

   - 서버가 PG 라우팅 후 결정된 PG와 flowType에 맞게 `nextAction` 구성
   - `SERVER_ONLY`/`NONE`인 경우 `status=CREATED` 또는 바로 승인 시도 가능

   ------

   ### 3.2 Confirm

   **POST /payments/{id}/confirm**

   요청(JSON)

   ```json
   {
     "idempotencyKey": "idem-confirm-123",
     "providerPayload": {
       "pgPaymentId": "toss_payment_123",  // PG사가 발급한 결제 식별자
       "pgToken": "xxx"
     }
   }
   ```

   응답(JSON)

   ```json
   {
     "paymentId": "pay-123",             // 우리 시스템의 payments.id
     "status": "SUCCESS | FAILED",
     "approvedAmount": 12000,
     "pg": {
       "provider": "toss",
       "pgPaymentId": "toss_payment_123", // PG사가 발급한 결제 식별자
       "raw": {}
     }
   }
   ```

   - Confirm은 flowType별 허용 상태에서만 호출 가능
     - CLIENT_SDK/REDIRECT → `REQUIRES_ACTION`
     - SERVER_ONLY → `CREATED` 또는 `REQUIRES_ACTION`
     - NONE → 항상 불가

   ------

   ## 4. 멱등성 전략

   - **Create 멱등키 (A)**
     - 클라가 생성 요청 시 넣은 `idempotencyKey`를 DB에 영속 (`payments.idempotency_key`)
     - 동일 요청 중복 방지 (1차 방어막)
   - **Confirm 멱등키 (B)**
     - `(pg_provider, pg_payment_id)`에 DB UNIQUE 제약 → 이중 승인 방지 (2차 방어막)
     - 필요 시 캐시 TTL(1–2h)로 응답 스냅샷 재방출
   - **스냅샷**
     - `{stage, request_hash, response_snapshot, status, created_at}` 보관

   ------

   ## 5. 데이터 모델 (핵심)

   - **payments**
     - `id, case_id, donor_id, amount, currency, status, pg_provider, pg_payment_id, idempotency_key, reason_code, reason_message, created_at, updated_at, paid_at`
   - **UNIQUE**
     - `idempotency_key` (Create)
     - `(pg_provider, pg_payment_id)` (Confirm)
   - **payment_events**
     - `payment_id, origin, event_id, event_type, raw_payload, created_at`
     - `raw_payload`에는 providerPayload 원본(JSON) 저장

   ------

   ## 6. 포트/어댑터 인터페이스

   ```java
   interface PgProviderPort {
     CreateOut createSession(CreateIn in);   // 필요 시 nextAction 생성
     ConfirmOut confirm(ConfirmIn in);       // 승인/캡처
   }
   ```

   - HY 전략: 표준 필드 + providerPayload/raw 패스스루

   ------

   ## 7. 검증 및 불변식

   - Create/Confirm 멱등키 필수
   - 금액 불변식: `approvedAmount ≤ amount`
   - 통화 일치 검증
   - 상태 전이는 허용된 경로만
   - Confirm은 flowType별 허용 상태에서만 호출 가능
     - `CLIENT_SDK` / `REDIRECT`
       - `REQUIRES_ACTION`
     - `SERVER_ONLY`
       - `CREATED`
       - `REQUIRES_ACTION`
     - `NONE`: 불가

   ------

   ## 8. 실패 및 재시도

   - 재시도 가능: `PG_TIMEOUT`, `NETWORK_ERROR`
   - 재시도 불가: `AMOUNT_MISMATCH`, `DECLINED_HARD`
   - SLA:
     - `REQUIRES_ACTION`: 사용자 대기 시간
     - `PROCESSING`: PG 응답 대기 시간

   ------

   ## 9. 관측/운영

   - 추후 운영 요구사항/조회 패턴 확정 시 확장 예정

   ------

   ## 10. 보안

   - 민감 페이로드는 KMS 암호화 저장, 접근 감사 로그
   - SecretKey는 서버에서만 사용, clientKey만 클라로 전달
   - 30일 보관 후 파기 정책

   ------

   ## 11. 구현 순서 (TODO)

   1. **DB 스키마 정의 및 마이그레이션** (payments, payment_events)
      - 마이그레이션 스크립트 작성 (V1__init.sql)
      - 롤백 전략/제약조건(UNIQUE, FK, 인덱스) 확정
   2. **상태머신 코드화** (CREATED → REQUIRES_ACTION → SUCCESS | FAILED)
      - 도메인 모델(엔티티/VO/Enum) 구현
      - **단위 테스트**: 전이 규칙/불변식 검증 완료
   3. **멱등키 관리** (Create/Confirm 단계별)
      - 키 스코프 정의(요청 파라미터 해시 포함 여부)
      - 중복 요청 충돌 처리/응답 재사용 정책
      - 단위 테스트
   4. **PgProviderPort 인터페이스 정의 및 TossAdapter 구현**
      - Port 시그니처/에러모델 확정
      - TossAdapter 1차 구현(+ 예외 매핑)
      - 단위 테스트 (어댑터, 포트 계약)
   5. **PaymentService.create() 구현**
      - 라우팅 + 멱등성 + 상태 전이
      - 금액/파라미터 유효성 검사
      - 단위 테스트
   6. **PaymentService.confirm() 구현**
      - 멱등성 + PG 호출 + 상태 전이
      - 실패/타임아웃 재시도 정책(있다면)
      - 단위 테스트
   7. **API Controller** (POST /payments, POST /payments/{id}/confirm)
      - 요청/응답 DTO, 예외 처리
      - 슬라이스 테스트(WebMvcTest)
   8. **단위 테스트 보강**
   9. **통합 테스트** (토스 플로우 end-to-end)
   10. **모니터링 지표/알람 구성**
