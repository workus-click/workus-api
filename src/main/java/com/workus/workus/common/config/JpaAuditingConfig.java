package com.workus.workus.common.config;

import com.workus.workus.common.session.Actor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // @CreatedDate, @LastModifiedDate 지원
    @Bean
    public AuditorAware<Long> auditorAware(){
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken))
            .map(Authentication::getPrincipal)
            .filter(Actor.class::isInstance)
            .map(Actor.class::cast)
            .map(Actor::getUserId);
    }
}
