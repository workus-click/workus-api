package com.workus.workus.payroll.employee.domain.model;

public enum PayType {
    HOURLY("시급"),
    DAILY("일급"),        // 현재 미사용
    MONTHLY("월급"),
    ANNUAL("연봉");        // 현재 미사용

    private final String description;

    PayType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}

