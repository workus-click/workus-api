package com.workus.workus.sample.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "test")
@Getter
public class TestEntity {
    @Id
    @Column
    private Integer id;

    @Column
    private String name;
}
