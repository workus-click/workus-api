package com.workus.workus.common.config;

import com.workus.workus.common.component.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // @CreatedDate, @LastModifiedDate 지원

    @Bean
    public AuditorAware<Long> auditorAware(){
        // 후에 인증사용자 ID로 변경 필요
        return () -> Optional.of(IdGenerator.nextId());
    }
}
