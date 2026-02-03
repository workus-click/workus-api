# 급여 계산식(Formula) 시스템 설계 Overview

## 📋 목차
- [시스템 개요](#시스템-개요)
- [데이터베이스 스키마](#데이터베이스-스키마)
- [객체 간 관계](#객체-간-관계)
- [역할과 책임](#역할과-책임)
- [데이터 흐름](#데이터-흐름)
- [설계 원칙](#설계-원칙)

---

## 시스템 개요

급여 계산식 시스템은 데이터베이스에 저장된 계산식 정보를 조회하여 도메인 객체로 변환하고, 계산을 수행하는 시스템입니다.

### 핵심 개념
- **Formula**: 계산식 표현식과 타입을 포함하는 Value Object
- **FormulaType**: 계산식의 타입을 나타내는 인터페이스 (지급항목/공제항목 구분)
- **SalaryFormula**: `salary_formula` 테이블의 엔티티
- **FormulaVariable**: `store_code` 테이블의 계산식 변수 정보 엔티티

---

## 데이터베이스 스키마

### 1. salary_formula 테이블
급여 계산식 설정 정보를 저장하는 테이블입니다.

**컬럼 구조**:
- `store_id` (BIGINT): 매장 ID
- `year` (CHAR(4)): 연도
- `worker_type` (CHAR(1)): 직원 구분 (E: 정규직, P: 알바)
- `salary_item_code_id` (BIGINT): 급여 항목 코드 ID (store_code 테이블 참조)
- `formula` (VARCHAR(500)): 계산식 표현식
- `base_time` (DECIMAL(10,2)): 기준 시간(분)
- `pay_rate` (DECIMAL(10,4)): 지급율
- `created_at`, `created_by`, `modify_at`, `modify_by`: 감사 필드

**Primary Key**: `(store_id, year, worker_type, salary_item_code_id)`

**역할**: 매장별, 연도별, 직원 구분별, 급여 항목별 계산식 설정을 저장합니다.

---

### 2. store_code 테이블 (FormulaVariable)
계산식에서 사용할 수 있는 변수 정보를 저장하는 테이블입니다.

**컬럼 구조**:
- `store_code_id` (BIGINT): 기본 키
- `store_id` (BIGINT): 매장 ID
- `code_type` (VARCHAR(20)): 코드 타입 (PAY_ITEM, DEDUCT_ITEM 등)
- `code` (VARCHAR(20)): 코드 값 (BASE_SALARY, OVERTIME_ALLOWANCE 등)
- `code_name` (VARCHAR(128)): 코드 이름

**역할**: 
- 계산식에서 사용 가능한 변수 목록을 정의합니다.
- `code_type`은 FormulaCategory와 매칭됩니다.
- `code`는 FormulaType enum의 name()과 매칭됩니다.

---

### 3. salary_calculation_formula 테이블
실제 적용된 계산식 정보를 저장하는 테이블입니다.

**컬럼 구조**:
- `formula_id` (BIGINT): 기본 키
- `store_id` (BIGINT): 매장 ID
- `formula_type` (VARCHAR): 계산식 타입 (FormulaType enum name())
- `formula` (TEXT): 계산식 표현식
- `created_at`, `created_by`, `modify_at`, `modify_by`: 감사 필드

**역할**: 
- `salary_formula` 테이블에서 조회한 계산식을 실제로 적용한 결과를 저장합니다.
- Formula Value Object가 `@Embedded`로 포함됩니다.

---

## 객체 간 관계

### 엔티티 관계도

```
┌─────────────────────────────────────────────────────────────┐
│                    Database Tables                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │  store_code      │         │  salary_formula   │        │
│  │                  │         │                   │        │
│  │  store_code_id ──┼─────────┼─► salary_item_    │        │
│  │  store_id        │         │    code_id        │        │
│  │  code_type       │         │  store_id         │        │
│  │  code            │         │  year             │        │
│  │  code_name       │         │  worker_type      │        │
│  └──────────────────┘         │  formula          │        │
│                                └──────────────────┘        │
│                                                              │
│                                ┌──────────────────┐        │
│                                │salary_calculation│        │
│                                │    _formula      │        │
│                                │                   │        │
│                                │  formula_id       │        │
│                                │  store_id         │        │
│                                │  formula_type     │        │
│                                │  formula          │        │
│                                └──────────────────┘        │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ 매핑
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Objects                           │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │ FormulaVariable  │         │  SalaryFormula   │        │
│  │   (Entity)       │         │    (Entity)      │        │
│  │                  │         │                   │        │
│  │  - store_code_id │         │  - id (PK)        │        │
│  │  - code_type     │         │  - formula        │        │
│  │  - code          │         │  - baseTime       │        │
│  │  - code_name     │         │  - payRate        │        │
│  └──────────────────┘         └──────────────────┘        │
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │  Formula (VO)    │         │SalaryCalculation │        │
│  │  @Embeddable     │         │   Formula        │        │
│  │                  │         │   (Entity)       │        │
│  │  - formulaType   │◄────────┤                   │        │
│  │  - expression    │ embedded │  - id            │        │
│  │                  │          │  - storeId       │        │
│  │  - calculate()   │          │  - formula (VO) │        │
│  │  - validate()    │          └──────────────────┘        │
│  └──────────────────┘                                      │
│                                                              │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │  FormulaType     │         │ SalaryFormula    │        │
│  │  (Interface)     │         │    Service       │        │
│  │                  │         │  (Domain Service)│        │
│  │  - getCategory() │         │                   │        │
│  └──────────────────┘         │  - findFormula()  │        │
│         ▲                     │  - existsFormula()│        │
│         │                     └──────────────────┘        │
│         │ implements                                      │
│  ┌──────┴──────┐                                           │
│  │PayItem      │  DeductItem                              │
│  │FormulaType  │  FormulaType                             │
│  └─────────────┘                                           │
└─────────────────────────────────────────────────────────────┘
```

### 테이블 간 관계

1. **store_code ↔ salary_formula**
   - `store_code.store_code_id` = `salary_formula.salary_item_code_id`
   - `store_code.code_type` = FormulaCategory (PAY_ITEM, DEDUCT_ITEM)
   - `store_code.code` = FormulaType enum name (BASE_SALARY, OVERTIME_ALLOWANCE 등)

2. **salary_formula → Formula → SalaryCalculationFormula**
   - `salary_formula` 테이블에서 조회한 데이터를 `Formula` Value Object로 변환
   - `Formula`는 `SalaryCalculationFormula` 엔티티에 `@Embedded`로 포함

---

## 역할과 책임

### 1. Formula (Value Object)
**책임**:
- 계산식 표현식(`formula` 컬럼)과 타입(`formula_type` 컬럼)을 캡슐화
- 계산 수행 로직 제공
- 변수 검증 및 문법 검증

**특징**:
- `@Embeddable`로 다른 엔티티에 임베드 가능
- `salary_calculation_formula` 테이블의 `formula_type`, `formula` 컬럼과 매핑
- 불변 객체로 설계

**데이터 소스**:
- `salary_formula.formula` → `Formula.expression`
- `FormulaType` → `Formula.formulaType` → `salary_calculation_formula.formula_type`

**주의사항**:
- Formula VO는 단순히 계산식 표현식과 타입을 담는 Value Object입니다.
- FormulaType별로 어떤 Formula를 생성할지는 Formula VO의 책임이 아닙니다.
- FormulaType별 생성 결정은 **SalaryFormulaService**에서 처리됩니다.

---

### 2. FormulaType (Interface)
**책임**:
- 계산식 타입을 나타내는 인터페이스
- 카테고리 정보 제공 (`getFormulaCategory()`)

**구현체**:
- `PayItemFormulaType`: 지급항목 계산식 타입
  - `BASE_SALARY`, `OVERTIME_ALLOWANCE`, `NIGHT_SHIFT_ALLOWANCE` 등
- `DeductItemFormulaType`: 공제항목 계산식 타입
  - `INCOME_TAX`, `NATIONAL_PENSION`, `HEALTH_INSURANCE` 등

**데이터베이스 매핑**:
- `FormulaType.name()` → `store_code.code`
- `FormulaType.getFormulaCategory().name()` → `store_code.code_type`

---

### 3. FormulaCategory (Enum)
**책임**:
- 계산식의 카테고리 분류

**값**:
- `PAY_ITEM`: 지급항목
- `DEDUCT_ITEM`: 공제항목
- `TOTAL_EARNINGS`: 차인지급액

**데이터베이스 매핑**:
- `FormulaCategory.name()` → `store_code.code_type`

---

### 4. SalaryFormula (Entity)
**책임**:
- `salary_formula` 테이블과 매핑
- 계산식 설정 정보 저장

**데이터베이스 매핑**:
- `SalaryFormula.id` → `salary_formula` 테이블의 복합 키
  - `store_id`, `year`, `worker_type`, `salary_item_code_id`
- `SalaryFormula.formula` → `salary_formula.formula` 컬럼

---

### 5. FormulaVariable (Entity)
**책임**:
- `store_code` 테이블과 매핑
- 계산식 변수 정보 제공

**데이터베이스 매핑**:
- `FormulaVariable.id` → `store_code.store_code_id`
- `FormulaVariable.codeType` → `store_code.code_type`
- `FormulaVariable.code` → `store_code.code`

**역할**:
- FormulaType과 salary_formula를 연결하는 브릿지 역할
- `store_code.code`로 FormulaType을 식별하고, `store_code_id`로 salary_formula를 조회

---

### 6. SalaryCalculationFormula (Entity)
**책임**:
- `salary_calculation_formula` 테이블과 매핑
- 실제 적용된 계산식 정보 저장

**데이터베이스 매핑**:
- `SalaryCalculationFormula.id` → `salary_calculation_formula.formula_id`
- `SalaryCalculationFormula.storeId` → `salary_calculation_formula.store_id`
- `SalaryCalculationFormula.formula` (embedded) → `salary_calculation_formula.formula_type`, `formula`

---

### 7. SalaryFormulaService (Domain Service)
**책임**:
- **FormulaType별 Formula 생성 결정 및 조정**
- 데이터베이스 조회와 Formula 생성의 연결 역할
- `salary_formula` 테이블에서 FormulaType별 계산식 조회
- 조회 결과를 Formula Value Object로 변환

**역할**:
- FormulaType을 받아서 해당 타입에 맞는 Formula를 생성하는 **조정자(Coordinator)** 역할
- Repository를 통해 FormulaType에 해당하는 데이터 조회
- Formula 클래스의 정적 팩토리 메서드를 호출하여 Formula 생성
- 도메인 로직과 인프라스트럭처 계층의 분리

**FormulaType별 생성 프로세스**:
1. FormulaType과 조회 조건(storeId, year, workerType)을 받음
2. Repository를 통해 FormulaType에 해당하는 `salary_formula` 데이터 조회
3. 조회한 `SalaryFormula` 엔티티를 `Formula.fromSalaryFormula()`로 변환
4. 변환된 Formula를 반환하거나 `SalaryCalculationFormula`로 래핑

**핵심 책임**:
- FormulaType별로 어떤 Formula를 생성할지 결정하는 것은 **Service 레이어의 책임**입니다.
- Formula VO는 단순히 변환만 담당하며, 생성 결정은 Service에서 처리합니다.

---

### 8. SalaryFormulaRepository (Infrastructure)
**책임**:
- `salary_formula` 테이블에서 FormulaType별 계산식 조회

**조회 프로세스**:
1. FormulaType의 `name()` 추출 (예: "BASE_SALARY")
2. FormulaType의 `getFormulaCategory().name()` 추출 (예: "PAY_ITEM")
3. `FormulaVariableRepository`로 `store_code` 테이블에서 해당 조건의 `store_code_id` 조회
   - 조건: `store_id`, `code_type`, `code`
4. 조회한 `store_code_id`를 `salary_item_code_id`로 사용하여 `salary_formula` 테이블 조회
   - 조건: `store_id`, `year`, `worker_type`, `salary_item_code_id`

---

## 데이터 흐름

### 1. 계산식 조회 및 적용 흐름

```
[Application Layer]
    │
    │ FormulaType 요청 (예: PayItemFormulaType.BASE_SALARY)
    │ 매개변수: storeId, year, workerType
    ▼
[SalaryFormulaService]
    │
    │ findByFormulaType() 호출
    ▼
[SalaryFormulaRepository]
    │
    │ 1. FormulaType.name() → "BASE_SALARY"
    │ 2. FormulaType.getFormulaCategory().name() → "PAY_ITEM"
    │ 3. store_code 테이블 조회
    │    WHERE store_id = ? AND code_type = 'PAY_ITEM' AND code = 'BASE_SALARY'
    │    → store_code_id 획득
    │ 4. salary_formula 테이블 조회
    │    WHERE store_id = ? AND year = ? AND worker_type = ? 
    │      AND salary_item_code_id = ?
    ▼
[SalaryFormula Entity]
    │
    │ Formula.fromSalaryFormula() 호출
    ▼
[Formula Value Object]
    │
    │ SalaryCalculationFormula.of() 호출
    ▼
[SalaryCalculationFormula Entity]
    │
    │ salary_calculation_formula 테이블에 저장
    ▼
[Database]
```

### 2. 계산 수행 흐름

```
[SalaryCalculationFormula Entity]
    │
    │ calculate(variableValues) 호출
    │ variableValues: Map<String, BigDecimal>
    │   - 키: 변수명 (store_code.code와 매칭)
    │   - 값: 변수 값
    ▼
[Formula Value Object]
    │
    │ 1. 변수 검증
    │    - variableValues의 키가 허용된 변수인지 확인
    │    - 누락된 변수 확인
    │ 2. 문법 검증
    │    - 계산식 표현식 문법 검증
    │ 3. 계산 수행
    │    - 변수 값 설정
    │    - 계산식 평가
    ▼
[BigDecimal 결과]
```

---

## 설계 원칙

### 1. 책임 분리 (Separation of Concerns)

**계층별 책임**:
- **Domain Model (Formula)**: 계산식 표현식 관리 및 계산 수행
- **Domain Service (SalaryFormulaService)**: DB 조회와 Formula 생성 연결
- **Infrastructure (Repository)**: 데이터베이스 조회 로직

**객체별 책임**:
- **Formula (VO)**: 계산 로직에만 집중
- **SalaryFormulaService**: 조회와 변환에만 집중
- **Repository**: 데이터 접근에만 집중

---

### 2. 단일 책임 원칙 (Single Responsibility)

각 객체는 하나의 명확한 책임을 가집니다:
- `Formula`: 계산식 표현식 관리 및 계산 수행
- `SalaryFormulaService`: FormulaType별 계산식 조회 및 Formula 생성
- `SalaryFormulaRepository`: `salary_formula` 테이블 조회
- `FormulaVariable`: `store_code` 테이블 매핑

---

### 3. 의존성 역전 원칙 (Dependency Inversion)

- Domain Service는 Repository 인터페이스에 의존
- Infrastructure Layer에서 구현체 제공
- Domain Layer는 Infrastructure Layer에 의존하지 않음

---

### 4. Value Object 패턴

- `Formula`는 불변 객체로 설계
- `@Embeddable`로 재사용 가능
- `SalaryCalculationFormula`에 임베드되어 사용

---

### 5. 테이블 중심 설계

- 모든 도메인 객체는 데이터베이스 테이블과 명확히 매핑
- 테이블 스키마 변경 시 엔티티만 수정하면 됨
- FormulaType enum의 `name()`이 `store_code.code`와 직접 매칭

---

## 테이블 간 관계 요약

### 매핑 체인

```
FormulaType (enum name)
    ↓
store_code.code
    ↓
store_code.store_code_id
    ↓
salary_formula.salary_item_code_id
    ↓
salary_formula.formula
    ↓
Formula.expression
    ↓
salary_calculation_formula.formula
```

### 조회 조건 체인

```
FormulaType 요청
    ↓
FormulaType.name() → store_code.code
FormulaType.getFormulaCategory().name() → store_code.code_type
    ↓
store_code 조회 (store_id, code_type, code)
    ↓
store_code_id 획득
    ↓
salary_formula 조회 (store_id, year, worker_type, salary_item_code_id)
    ↓
formula 컬럼 획득
    ↓
Formula Value Object 생성
```

---

## 주요 특징

### ✅ 설계 장점

1. **테이블 중심 설계**: 모든 객체가 명확한 테이블 매핑을 가짐
2. **명확한 책임 분리**: 각 객체가 단일 책임을 가짐
3. **타입 안정성**: FormulaType enum으로 타입 안정성 보장
4. **확장성**: 새로운 FormulaType 추가 시 enum만 추가하면 됨
5. **재사용성**: Formula VO는 여러 엔티티에서 재사용 가능

### 🔄 확장 가능성

- **새로운 FormulaType 추가**: enum에 값 추가, `store_code` 테이블에 데이터 추가
- **계산 로직 변경**: Formula 클래스만 수정
- **DB 스키마 변경**: Repository와 Entity만 수정

---

## 참고사항

### FormulaType과 데이터베이스 매칭 규칙

- FormulaType enum의 `name()`이 `store_code.code`와 정확히 일치해야 함
- 예: `PayItemFormulaType.BASE_SALARY.name()` = `"BASE_SALARY"` = `store_code.code`
- FormulaType의 `getFormulaCategory().name()`이 `store_code.code_type`과 일치해야 함
- 예: `FormulaCategory.PAY_ITEM.name()` = `"PAY_ITEM"` = `store_code.code_type`

### 변수 관리

- 계산식에서 사용할 변수는 `store_code` 테이블에서 `code_type`별로 조회
- 변수명은 `store_code.code`와 매칭
- `variableValues`의 키가 허용된 변수 목록으로 사용됨

### 계산식 표현식

- `salary_formula.formula` 컬럼에 저장된 문자열 표현식
- 변수명은 `store_code.code`와 매칭되어야 함
- 예: "BASE_SALARY + OVERTIME_ALLOWANCE"에서 `BASE_SALARY`, `OVERTIME_ALLOWANCE`는 `store_code.code`와 일치해야 함
