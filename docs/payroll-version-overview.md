# Payroll 계산식 버전 관리 설계

## 요구사항

- 계산식은 INSERT만 가능 (Immutable)
- 계산식 추가/삭제 시 새 버전 생성
- 각 FormulaType별 하나의 계산식만 존재

## 검토한 방안

| 방안 | 설명 | 단점 |
|------|------|------|
| 다대다 관계 | Version ↔ Formula 중간 테이블 | 복잡, JOIN 필요 |
| 1:N 관계 | Formula에 versionId 포함 | 계산식 데이터 중복 복사 |
| 유효 버전 범위 | Formula에 from/to 버전 저장 | 조회 쿼리 복잡 |
| **타입별 컬럼 (채택)** | Version에 타입별 formulaId 컬럼 | 컬럼 수 많음 (16개) |

## 최종 결정: 타입별 컬럼

**선택 이유:**
- 급여 항목은 법적 규정으로 자주 변경되지 않음
- 단순한 구조, 중간 테이블 불필요
- 타입별 단일 계산식 규칙이 스키마에 반영됨
- 버전 단일 조회로 모든 계산식 ID 확인 가능

## 테이블 구조

```
salary_calculation_formula_version
├── version_id, store_id, version_number
├── base_salary_formula_id, overtime_allowance_formula_id, ...
└── income_tax_formula_id, national_pension_formula_id, ...

salary_calculation_formula (Immutable)
├── formula_id, store_id
├── formula_type
└── formula (expression)
```

## API

| 메서드 | 설명 |
|--------|------|
| `addFormula(storeId, type, expression)` | 추가 + 새 버전 |
| `deactivateFormula(storeId, type)` | 미사용 처리 + 새 버전 (버전에서 null 처리, 계산식 데이터 유지) |
| `replaceFormula(storeId, type, expression)` | 교체 + 새 버전 |

## 계산식 변수 (FormulaVariable)

### 개요

계산식은 숫자가 아닌 **변수 코드와 연산 기호**로 구성됨.

```
예시: BASE_SALARY * WORK_HOURS + OVERTIME_RATE * OVERTIME_HOURS
```

### 테이블 매핑

`FormulaVariable`은 공통 코드 테이블(`store_code`)을 도메인 관점으로 매핑한 엔티티.

```
store_code 테이블
├── store_code_id (PK)
├── store_id
├── code_type    ← PAY_ITEM, DEDUCT_ITEM 등
├── code         ← 변수 코드 (BASE_SALARY 등)
└── code_name    ← 변수명 (기본급 등)
```

### 설계 의도

- 같은 테이블이지만 **Bounded Context**에 따라 `FormulaVariable`로 명명
- `codeType` 조건으로 계산식에 필요한 변수만 조회
- 향후 계산식 검증, 변수 치환 등 도메인 특화 메서드 제공 예정

### 사용 예시

```java
// PAY_ITEM 카테고리의 변수 코드 조회
Set<String> allowedVariables = formulaVariableRepository
    .findCodesByStoreIdAndCodeType(storeId, "PAY_ITEM");

// 계산식 유효성 검증 시 허용된 변수만 사용 가능
formula.calculate(allowedVariables, values);
```
