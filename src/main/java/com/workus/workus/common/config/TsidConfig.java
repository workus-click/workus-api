package com.workus.workus.common.config;

import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
@Configuration
public class TsidConfig {

    @Value("${id-gen.tsid.node:#{null}}")
    private Integer node;

    @Value("${id.generator.tsid.node-bits}")
    private int nodeBits;

    @Value("${id.generator.tsid.epoch}")
    private String epoch;

    @Bean
    public TsidFactory pkTsidFactory() {
        return TsidFactory.builder()
                .withNode(node) // node 값이 null 이면 자동 할당
                .withNodeBits(nodeBits)
                .withCustomEpoch(Instant.parse(epoch))
                .build();
    }
}

