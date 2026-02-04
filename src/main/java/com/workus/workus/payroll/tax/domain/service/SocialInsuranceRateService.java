package com.workus.workus.payroll.tax.domain.service;

import com.workus.workus.payroll.tax.domain.model.SocialInsuranceRate;
import com.workus.workus.payroll.tax.domain.repository.SocialInsuranceRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialInsuranceRateService {

    private final SocialInsuranceRateRepository socialInsuranceRateRepository;

    /**
     * 연도, 보험종류로 요율 조회
     */
    public Optional<SocialInsuranceRate> getRate(String year, String insuranceType) {
        return socialInsuranceRateRepository.findByYearAndInsuranceType(year, insuranceType);
    }

    /**
     * 연도, 보험종류로 사원 부담 요율 조회
     */
    public BigDecimal getEmployeeRate(String year, String insuranceType) {
        return socialInsuranceRateRepository.findByYearAndInsuranceType(year, insuranceType)
                .map(SocialInsuranceRate::getEmployeeRate)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 연도, 보험종류로 회사 부담 요율 조회
     */
    public BigDecimal getEmployerRate(String year, String insuranceType) {
        return socialInsuranceRateRepository.findByYearAndInsuranceType(year, insuranceType)
                .map(SocialInsuranceRate::getEmployerRate)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 연도별 전체 보험 요율 조회
     */
    public List<SocialInsuranceRate> getRatesByYear(String year) {
        return socialInsuranceRateRepository.findAllByYear(year);
    }

    /**
     * 연도별 보험종류 -> 요율 Map 조회
     */
    public Map<String, SocialInsuranceRate> getRateMapByYear(String year) {
        return socialInsuranceRateRepository.findAllByYear(year).stream()
                .collect(Collectors.toMap(
                        SocialInsuranceRate::getInsuranceType,
                        rate -> rate
                ));
    }

    /**
     * 연도별 보험종류 -> 사원부담요율 Map 조회
     */
    public Map<String, BigDecimal> getEmployeeRateMapByYear(String year) {
        return socialInsuranceRateRepository.findAllByYear(year).stream()
                .collect(Collectors.toMap(
                        SocialInsuranceRate::getInsuranceType,
                        SocialInsuranceRate::getEmployeeRate
                ));
    }

    /**
     * 연도별 보험종류 -> 회사부담요율 Map 조회
     */
    public Map<String, BigDecimal> getEmployerRateMapByYear(String year) {
        return socialInsuranceRateRepository.findAllByYear(year).stream()
                .collect(Collectors.toMap(
                        SocialInsuranceRate::getInsuranceType,
                        SocialInsuranceRate::getEmployerRate
                ));
    }
}
