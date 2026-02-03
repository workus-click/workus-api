package com.workus.workus.payroll.formula.presentation.controller;

import com.workus.workus.common.presentation.dto.Response;
import com.workus.workus.payroll.formula.domain.model.DeductItemFormulaType;
import com.workus.workus.payroll.formula.domain.model.FormulaType;
import com.workus.workus.payroll.formula.domain.model.PayItemFormulaType;
import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormula;
import com.workus.workus.payroll.formula.domain.model.SalaryCalculationFormulaVersion;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaRepository;
import com.workus.workus.payroll.formula.domain.repository.SalaryCalculationFormulaVersionRepository;
import com.workus.workus.payroll.formula.domain.service.SalaryCalculationFormulaService;
import com.workus.workus.payroll.formula.presentation.dto.AddFormulaRequest;
import com.workus.workus.payroll.formula.presentation.dto.FormulaDetailResponse;
import com.workus.workus.payroll.formula.presentation.dto.FormulaVersionResponse;
import com.workus.workus.payroll.formula.presentation.dto.ReplaceFormulaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "급여 계산식", description = "급여 계산식 버전 관리 API")
@RestController
@RequestMapping("/api/payroll/formula")
@RequiredArgsConstructor
public class SalaryFormulaController {
    private final SalaryCalculationFormulaService formulaService;
    private final SalaryCalculationFormulaRepository formulaRepository;
    private final SalaryCalculationFormulaVersionRepository versionRepository;

    /**
     * 최신 버전 조회 (계산식 상세 정보 포함)
     */
    @Operation(summary = "최신 버전 조회", description = "매장의 최신 급여 계산식 버전과 계산식 상세 정보를 조회합니다.")
    @GetMapping
    public Response<FormulaVersionResponse> getLatestVersion(
            @Parameter(description = "매장 ID") @RequestParam Long storeId
    ) {
        return formulaService.getLatestVersion(storeId)
                .map(version -> {
                    List<Long> formulaIds = version.getAllFormulaIds();
                    List<SalaryCalculationFormula> formulas = formulaRepository.findAllByIdIn(formulaIds);
                    return Response.of(0L, "성공", FormulaVersionResponse.from(version, formulas));
                })
                .orElse(Response.of(-1L, "버전이 존재하지 않습니다.", null));
    }

    /**
     * 모든 버전 조회 (계산식 상세 정보 포함)
     */
    @Operation(summary = "모든 버전 조회", description = "매장의 모든 급여 계산식 버전 이력과 계산식 상세 정보를 조회합니다.")
    @GetMapping("/versions")
    public Response<List<FormulaVersionResponse>> getAllVersions(
            @Parameter(description = "매장 ID") @RequestParam Long storeId
    ) {
        List<SalaryCalculationFormulaVersion> versionList = formulaService.getAllVersions(storeId);
        
        // 모든 버전의 formulaId를 수집
        List<Long> allFormulaIds = versionList.stream()
                .flatMap(v -> v.getAllFormulaIds().stream())
                .distinct()
                .toList();
        
        // 한 번에 조회
        List<SalaryCalculationFormula> allFormulas = formulaRepository.findAllByIdIn(allFormulaIds);
        
        List<FormulaVersionResponse> versions = versionList.stream()
                .map(version -> FormulaVersionResponse.from(version, allFormulas))
                .toList();
        
        return Response.of(0L, "성공", versions);
    }

    /**
     * 특정 버전 조회 (계산식 상세 정보 포함)
     */
    @Operation(summary = "특정 버전 조회", description = "매장의 특정 버전 급여 계산식을 조회합니다.")
    @GetMapping("/versions/{versionNumber}")
    public Response<FormulaVersionResponse> getVersionByNumber(
            @Parameter(description = "버전 번호") @PathVariable Integer versionNumber,
            @Parameter(description = "매장 ID") @RequestParam Long storeId
    ) {
        return versionRepository.findByStoreIdAndVersionNumber(storeId, versionNumber)
                .map(version -> {
                    List<Long> formulaIds = version.getAllFormulaIds();
                    List<SalaryCalculationFormula> formulas = formulaRepository.findAllByIdIn(formulaIds);
                    return Response.of(0L, "성공", FormulaVersionResponse.from(version, formulas));
                })
                .orElse(Response.of(-1L, "해당 버전이 존재하지 않습니다. versionNumber: " + versionNumber, null));
    }

    /**
     * 계산식 단건 조회
     */
    @Operation(summary = "계산식 단건 조회", description = "계산식 ID로 단건 조회합니다.")
    @GetMapping("/detail/{formulaId}")
    public Response<FormulaDetailResponse> getFormulaById(
            @Parameter(description = "계산식 ID") @PathVariable Long formulaId
    ) {
        return formulaRepository.findById(formulaId)
                .map(formula -> Response.of(0L, "성공", FormulaDetailResponse.from(formula)))
                .orElse(Response.of(-1L, "계산식을 찾을 수 없습니다. formulaId: " + formulaId, null));
    }

    /**
     * 계산식 추가
     */
    @Operation(summary = "계산식 추가", description = "새로운 계산식을 추가하고 새 버전을 생성합니다. formulaType: BASE_SALARY, OVERTIME_ALLOWANCE, NIGHT_SHIFT_ALLOWANCE, HOLIDAY_ALLOWANCE, WEEKLY_HOLIDAY_ALLOWANCE, INCOME_TAX, LOCAL_INCOME_TAX, NATIONAL_PENSION, HEALTH_INSURANCE, LONG_TERM_CARE_INSURANCE, EMPLOYMENT_INSURANCE 등")
    @PostMapping
    public Response<FormulaVersionResponse> addFormula(
            @Valid @RequestBody AddFormulaRequest request
    ) {
        FormulaType formulaType = parseFormulaType(request.formulaType());
        if (formulaType == null) {
            return Response.of(-1L, "잘못된 계산식 타입입니다: " + request.formulaType(), null);
        }

        SalaryCalculationFormulaVersion version = formulaService.addFormula(
                request.storeId(),
                formulaType,
                request.expression()
        );

        List<Long> formulaIds = version.getAllFormulaIds();
        List<SalaryCalculationFormula> formulas = formulaRepository.findAllByIdIn(formulaIds);
        return Response.of(0L, "성공", FormulaVersionResponse.from(version, formulas));
    }

    /**
     * 계산식 교체
     */
    @Operation(summary = "계산식 교체", description = "기존 계산식을 새 계산식으로 교체하고 새 버전을 생성합니다.")
    @PutMapping("/{formulaType}")
    public Response<FormulaVersionResponse> replaceFormula(
            @Parameter(description = "계산식 타입 (예: BASE_SALARY, INCOME_TAX)") @PathVariable String formulaType,
            @Valid @RequestBody ReplaceFormulaRequest request
    ) {
        FormulaType type = parseFormulaType(formulaType);
        if (type == null) {
            return Response.of(-1L, "잘못된 계산식 타입입니다: " + formulaType, null);
        }

        SalaryCalculationFormulaVersion version = formulaService.replaceFormula(
                request.storeId(),
                type,
                request.expression()
        );

        List<Long> formulaIds = version.getAllFormulaIds();
        List<SalaryCalculationFormula> formulas = formulaRepository.findAllByIdIn(formulaIds);
        return Response.of(0L, "성공", FormulaVersionResponse.from(version, formulas));
    }

    /**
     * 계산식 미사용 처리
     */
    @Operation(summary = "계산식 미사용 처리", description = "계산식을 미사용 처리하고 새 버전을 생성합니다. (실제 데이터는 삭제되지 않음)")
    @DeleteMapping("/{formulaType}")
    public Response<FormulaVersionResponse> deactivateFormula(
            @Parameter(description = "계산식 타입 (예: BASE_SALARY, INCOME_TAX)") @PathVariable String formulaType,
            @Parameter(description = "매장 ID") @RequestParam Long storeId
    ) {
        FormulaType type = parseFormulaType(formulaType);
        if (type == null) {
            return Response.of(-1L, "잘못된 계산식 타입입니다: " + formulaType, null);
        }

        SalaryCalculationFormulaVersion version = formulaService.deactivateFormula(storeId, type);

        List<Long> formulaIds = version.getAllFormulaIds();
        List<SalaryCalculationFormula> formulas = formulaRepository.findAllByIdIn(formulaIds);
        return Response.of(0L, "성공", FormulaVersionResponse.from(version, formulas));
    }

    /**
     * 문자열을 FormulaType으로 변환
     */
    private FormulaType parseFormulaType(String typeString) {
        // PayItemFormulaType 검색
        for (PayItemFormulaType type : PayItemFormulaType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }

        // DeductItemFormulaType 검색
        for (DeductItemFormulaType type : DeductItemFormulaType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }

        return null;
    }
}
