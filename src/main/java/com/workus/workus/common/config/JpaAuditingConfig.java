package com.workus.workus.common.config;

import com.workus.workus.common.session.Actor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // @CreatedDate, @LastModifiedDate 지원
    @Bean
    public AuditorAware<Long> auditorAware(){
        return () -> Optional.of(resolveAuditorId());
    }
    private Long resolveAuditorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user for auditing");
        }
		return ((Actor) authentication.getPrincipal()).getUserId();
    }
}
