# CareBridge 도메인 모델

## 도메인 모델 만들기
1. 듣고 배우기
2. '중요한 것'들 찾기 (개념 식별)
3. '연결 고리' 찾기 (관계 정의)
4. '것'들 설명하기 (속성 및 기본 행위 명시)
5. 그려보기 (시각화)
6. 이야기 하고 다듬기 (반복)

## CareBridge 도메인

- CareBridge는 도움이 필요한 사람들을 위한 투명한 후원 플랫폼이다.

- 수혜자는 도움이 필요한 사례(Case)를 등록한다.
  - Case는 심사를 거쳐 공개되고, 후원 모집을 시작한다.
  - Case는 목표 금액과 마감일을 가진다.

- 후원자(Donor)는 공개된 Case에 후원(Donation)을 할 수 있다.
  - 후원 금액을 정하고 "후원하기" 버튼을 클릭하면 Donation이 생성된다.
  - Donation이 생성되면 자동으로 결제(Payment)가 시작된다.

- 결제(Payment)는 PG사(토스페이먼츠 등)를 통해 처리된다.
  - 결제 실패 시 같은 Donation에 대해 재시도할 수 있다.
  - 재시도는 완전히 새로운 Payment 엔티티를 생성한다.
  - 한 Donation에 여러 Payment 시도가 기록된다.

- 후원금이 모이면 수혜자(Beneficiary)가 지급 요청서(Receipt)를 제출한다.
  - Receipt는 "어떤 지출에 얼마가 필요한지" 기록한다 (예: "병원비 영수증 800만원").
  - 운영자가 Receipt를 검토하고 승인한다.
  - 승인되면 Disbursement(지급)가 생성된다.
  - 영수증에 나온 가상계좌로 실제 입금이 진행된다.

- 입금이 완료되면 Disbursement가 증빙으로 기록된다.
  - 후원자는 "내 후원금 중 얼마가 이 영수증에 사용되었는지" 확인할 수 있다.
  - 한 Case에 여러 Receipt/Disbursement가 존재할 수 있다.

- 후원의 투명성이 핵심 가치이다.
  - 후원금이 어디에 얼마나 사용되었는지 명확히 기록된다.
  - 후원자는 자신의 후원이 실제로 사용된 내역을 확인할 수 있다.

---

## 도메인 모델

### Donation (후원)
*Entity*

#### 개념
- 후원자가 특정 케이스에 후원하겠다는 의사 표현
- 결제 실패/재시도와 무관하게 동일한 ID 유지
- 이커머스의 Order(주문)와 동일한 개념
- 결제(Payment)는 Donation을 이행하기 위한 수단

#### 속성
- `id`: String (UUID) - 후원 식별자
- `caseId`: String - 어떤 케이스에 대한 후원인지
- `donorId`: String - 누가 후원했는지
- `amount`: Long - 후원 금액
- `currency`: Currency - 통화 (KRW)
- `status`: DonationStatus - 후원 상태
- `createdAt`: LocalDateTime - 생성 시각
- `completedAt`: LocalDateTime - 완료 시각 (nullable)

#### 행위
- `static create(caseId, donorId, amount, currency)`: 새 후원 생성
- `markCompleted()`: 결제 성공 시 완료 처리
- `cancel(reason)`: 사용자가 후원 취소
- `expire()`: 시간 초과로 만료
- `isCompleted()`: 완료 여부 확인

#### 규칙 (불변식)
- 생성 시 상태는 PENDING
- amount는 0보다 커야 함
- Case가 FUNDING 상태일 때만 생성 가능
- caseId, donorId, PENDING 상태인 후원이 있으면 새로운 donation 생성 불가
- PENDING → COMPLETED (PAID 상태의 Payment 필요)
- PENDING → CANCELLED (사용자 취소)
- PENDING → EXPIRED (시간 초과)
- 최종 상태(COMPLETED/CANCELLED/EXPIRED)는 변경 불가
- PENDING 상태에서만 createPayment() 가능
- COMPLETED 상태이면 반드시 PAID 상태의 Payment 1개 존재

#### 관계
- Case (N:1) - 한 Case에 여러 Donation
- Donor (N:1) - 한 Donor가 여러 Donation
- Payment (1:N) - 한 Donation에 여러 Payment 시도 가능

---

### DonationStatus (후원 상태)
*Enum*

#### 상수
- `PENDING`: 결제 대기 중
- `COMPLETED`: 결제 완료, 후원 확정
- `CANCELLED`: 사용자 취소
- `EXPIRED`: 시간 초과로 만료

#### 상태 전이
- PENDING → COMPLETED (결제 성공)
- PENDING → CANCELLED (사용자 취소)
- PENDING → EXPIRED (만료)
- COMPLETED/CANCELLED/EXPIRED는 최종 상태 (변경 불가)

---

### Payment (결제)
*Entity*

#### 개념
- Donation을 이행하기 위한 결제 시도(Attempt)
- Donation에 종속되며, Donation 없이 존재할 수 없음
- 한 Donation에 여러 Payment 시도 가능 (결제 실패 시 재시도)
- 재시도 시 완전히 새로운 Payment 엔티티 생성 (PG사 세션도 새로 발급)
- PG사와 실제로 통신하는 기술적 경계

#### 속성
- `id`: String (UUID) - 결제 식별자
- `donationId`: String - 어떤 후원에 대한 결제인지
- `amount`: Long - 결제 금액
- `currency`: Currency - 통화 (KRW)
- `status`: PaymentStatus - 결제 상태
- `idempotencyKey`: String - 멱등성 키
- `pgProviderId`: Long - PG사 (FK to PgProvider)
- `pgPaymentId`: String - PG사가 발급한 결제 ID (nullable)
- `reason`: Reason - 실패/취소 사유 (nullable, Embedded)
- `createdAt`: LocalDateTime - 생성 시각
- `paidAt`: LocalDateTime - 결제 완료 시각 (nullable)

#### 행위
- `static create(donationId, amount, currency, pgProvider, idempotencyKey)`: 새 결제 생성
- `markRequiresAction()`: 사용자 액션 필요 상태로 전이 (가상계좌 등)
- `markPaid(pgPaymentId)`: 결제 완료 처리
- `markFailedFromPg(pgCode, pgMessage)`: PG사 오류로 실패 처리
- `markFailedFromSystem(code, message)`: 시스템 오류로 실패 처리
- `cancelByUser(code, message)`: 사용자가 취소
- `isCreated()`: CREATED 상태 여부
- `isRequiresAction()`: 액션 대기 상태 여부
- `isPaid()`: 결제 완료 여부
- `isFailed()`: 실패 여부
- `isCancelled()`: 취소 여부

#### 규칙 (불변식)
- 생성 시 상태는 CREATED
- amount는 0보다 커야 함
- Donation의 amount와 동일해야 함
- pgProvider는 필수 (null 불가)
- 상태 전이는 PgFlowType에 따라 다름:
  - **CLIENT_SDK (토스, 카드 결제)**: CREATED → PAID/FAILED/CANCELLED
  - **SERVER_ONLY (가상계좌)**: CREATED → REQUIRES_ACTION → PAID/CANCELLED
- PAID 상태이면 반드시 pgPaymentId 존재
- FAILED/CANCELLED 상태이면 반드시 reason 존재
- 최종 상태(PAID/FAILED/CANCELLED)는 변경 불가

#### 책임 범위
**Payment가 할 일:**
- 상태 관리 (CREATED → REQUIRES_ACTION → PAID/FAILED/CANCELLED)
- PG 결제 ID 저장 (pgPaymentId)
- 실패 사유 기록 (Reason)
- 결제 완료 시각 기록 (paidAt)

**Payment가 하지 말아야 할 일:**
- ❌ PG사와 직접 HTTP 통신 (→ PaymentExecutor가 담당)
- ❌ Donation 상태 변경 (→ DonationService가 이벤트 기반으로 처리)
- ❌ Case 금액 업데이트 (→ CaseService가 담당)

#### 관계
- Donation (N:1) - 한 Donation에 여러 Payment 시도
- PgProvider (N:1) - 한 PgProvider로 여러 Payment 처리

---

### PaymentStatus (결제 상태)
*Enum*

#### 상수
- `CREATED`: 생성됨
- `REQUIRES_ACTION`: 사용자 액션 필요 (가상계좌 입금 대기 등)
- `PAID`: 결제 완료
- `FAILED`: 결제 실패
- `CANCELLED`: 취소됨

#### 상태 전이 (PgFlowType별)
**CLIENT_SDK (토스, 카드 즉시 결제):**
- CREATED → PAID (승인 성공)
- CREATED → FAILED (승인 거절)
- CREATED → CANCELLED (사용자 취소)

**SERVER_ONLY (가상계좌, 비동기 입금):**
- CREATED → REQUIRES_ACTION (가상계좌 발급됨, 입금 대기)
- REQUIRES_ACTION → PAID (입금 확인)
- REQUIRES_ACTION → CANCELLED (기간 만료, 취소)

---

### Currency (통화)
*Enum*

#### 상수
- `KRW`: 한국 원화

---

### Reason (실패/취소 사유)
*Value Object (Embedded)*

#### 개념
- Payment 실패 또는 취소 사유를 기록
- 사유의 출처(Origin)에 따라 코드 정규화

#### 속성
- `code`: String - 사유 코드 (예: PG_CARD_LIMIT_EXCEEDED)
- `message`: String - 사유 메시지

#### 행위
- `static pg(rawCode, rawMessage)`: PG사 오류 사유 생성
- `static system(rawCode, rawMessage)`: 시스템 오류 사유 생성
- `static user(rawCode, rawMessage)`: 사용자 취소 사유 생성

#### 규칙
- code는 "출처_원본코드" 형식으로 정규화 (예: PG_LIMIT_EXCEEDED)
- 원본 코드가 없으면 "출처_UNKNOWN" 사용

---

### Origin (사유 출처)
*Enum*

#### 상수
- `SYSTEM` (코드: SYS) - 시스템 오류
- `PG` (코드: PG) - PG사 오류
- `USER` (코드: USR) - 사용자 액션

---

### PgProvider (PG 제공사)
*Entity*

#### 개념
- 결제 대행사 (Payment Gateway Provider)
- 토스페이먼츠, 페이팔 등

#### 속성
- `id`: Long - PK
- `code`: PgProviderCode - PG사 코드
- `name`: String - PG사 이름
- `status`: PgStatus - 사용 가능 여부
- `flowType`: PgFlowType - 결제 플로우 타입

#### 행위
- `assertConfirmable(PaymentStatus)`: 현재 상태에서 결제 승인 가능한지 검증

#### 규칙
- flowType에 따라 허용되는 상태 전이가 다름

#### 관계
- PgAccount (1:N) - 한 PG사에 여러 계정 (환경별, 테스트/프로덕션)

---

### PgProviderCode (PG사 코드)
*Enum*

#### 상수
- `TOSS`: 토스페이먼츠

---

### PgFlowType (결제 플로우 타입)
*Enum*

#### 개념
- PG사별로 다른 결제 처리 방식을 추상화
- Payment 상태 전이 규칙이 flowType에 따라 다름

#### 상수
- `CLIENT_SDK`: 클라이언트 SDK 방식 (토스 카드 결제)
  - 클라이언트에서 결제창 오픈
  - 백엔드는 승인만 처리
  - 허용 상태: REQUIRES_ACTION에서만 confirm 가능

- `REDIRECT`: 리다이렉트 방식 (페이팔 등)
  - 외부 URL로 리다이렉트
  - 허용 상태: REQUIRES_ACTION에서만 confirm 가능

- `SERVER_ONLY`: 서버 간 통신 (가상계좌, 계좌이체)
  - 백엔드에서 모든 처리
  - 허용 상태: CREATED 또는 REQUIRES_ACTION에서 confirm 가능

- `NONE`: 결제 없음 (무료)

#### 행위
- `isConfirmableFrom(PaymentStatus)`: 현재 상태에서 confirm 가능한지 확인
- `error(PaymentStatus)`: 불가능한 상태일 때 오류 메시지 생성

---

### PaymentEvent (결제 이벤트)
*Entity*

#### 개념
- Payment의 모든 중요한 상태 변화를 기록
- 이벤트 소싱, 디버깅, 감사 목적

#### 속성
- `id`: Long - PK (auto increment)
- `paymentId`: String - 어떤 결제에 대한 이벤트인지
- `origin`: Origin - 이벤트 출처
- `eventId`: String - 이벤트 고유 ID (중복 방지)
- `eventType`: PaymentEventType - 이벤트 타입
- `rawPayload`: String - PG사 원본 응답 (JSON)
- `isPublished`: Boolean - 이벤트 발행 여부
- `createdAt`: LocalDateTime - 이벤트 발생 시각

#### 행위
- `static create(paymentId, rawPayload)`: 결제 생성 이벤트
- `static confirm(paymentId, rawPayload)`: 결제 승인 이벤트
- `markAsPublished()`: 이벤트 발행 완료 표시

#### 규칙
- eventId는 unique (중복 이벤트 방지)

---

### PaymentEventType (결제 이벤트 타입)
*Enum*

#### 상수
- `CREATED`: 결제 생성됨
- `CONFIRMED`: 결제 승인됨
- `FAILED`: 결제 실패
- `CANCELLED`: 결제 취소

---

## TODO: 나머지 도메인

### Case
- TBD

### Donor (User)
- TBD

### Beneficiary
- TBD

### Receipt
- TBD

### Disbursement
- TBD
