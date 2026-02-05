package com.workus.workus.payroll.deduction.infra.repository;

import com.workus.workus.payroll.deduction.domain.model.SocialInsuranceRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaSocialInsuranceRateRepository extends JpaRepository<SocialInsuranceRate, Long> {

    Optional<SocialInsuranceRate> findByYearAndInsuranceType(String year, String insuranceType);

    List<SocialInsuranceRate> findAllByYear(String year);
}
