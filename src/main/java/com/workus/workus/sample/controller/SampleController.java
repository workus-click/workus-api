package com.workus.workus.sample.controller;

import com.workus.workus.common.component.IdGenerator;
import com.workus.workus.sample.domain.TestEntity;
import com.workus.workus.sample.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleController {
    private final SampleService sampleService;

    @GetMapping("/test")
    public List<TestEntity> getTestEntities(){
        return sampleService.test();
    }
    @GetMapping("/nextId")
    public Long testNextId() {
        return IdGenerator.nextId();
    }
}
