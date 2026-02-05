package com.workus.workus.payroll.deduction.infra.repository;

import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceRate;
import com.workus.workus.payroll.deduction.domain.repository.SocialInsuranceRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SocialInsuranceRateRepositoryImpl implements SocialInsuranceRateRepository {

    private final JpaSocialInsuranceRateRepository jpaRepository;

    @Override
    public Optional<SocialInsuranceRate> findByYearAndInsuranceType(String year, String insuranceType) {
        return jpaRepository.findByYearAndInsuranceType(year, insuranceType);
    }

    @Override
    public List<SocialInsuranceRate> findAllByYear(String year) {
        return jpaRepository.findAllByYear(year);
    }
}
