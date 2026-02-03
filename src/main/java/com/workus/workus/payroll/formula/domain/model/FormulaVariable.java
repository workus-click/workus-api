package com.workus.workus.payroll.formula.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "store_code")
@Getter
public class FormulaVariable {
    @Id
    @Column(name = "store_code_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "code_type", nullable = false, length = 20)
    private String codeType;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "code_name", nullable = false, length = 128)
    private String name;
}
