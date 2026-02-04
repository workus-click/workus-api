package com.workus.workus.payroll.tax.domain.repository;

import com.workus.workus.payroll.tax.domain.model.SocialInsuranceRate;

import java.util.List;
import java.util.Optional;

public interface SocialInsuranceRateRepository {

    Optional<SocialInsuranceRate> findByYearAndInsuranceType(String year, String insuranceType);

    List<SocialInsuranceRate> findAllByYear(String year);
}
