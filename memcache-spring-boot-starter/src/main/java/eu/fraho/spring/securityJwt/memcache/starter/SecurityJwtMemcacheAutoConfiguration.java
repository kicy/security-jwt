/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.memcache.starter;

import eu.fraho.spring.securityJwt.config.RefreshProperties;
import eu.fraho.spring.securityJwt.memcache.config.MemcacheProperties;
import eu.fraho.spring.securityJwt.memcache.service.MemcacheTokenStore;
import eu.fraho.spring.securityJwt.service.RefreshTokenStore;
import eu.fraho.spring.securityJwt.starter.SecurityJwtBaseAutoConfiguration;
import eu.fraho.spring.securityJwt.starter.SecurityJwtNoRefreshStoreAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@AutoConfigureAfter(SecurityJwtBaseAutoConfiguration.class)
@AutoConfigureBefore(SecurityJwtNoRefreshStoreAutoConfiguration.class)
@Slf4j
public class SecurityJwtMemcacheAutoConfiguration {
    @Bean
    @ConditionalOnBean(RefreshTokenStore.class)
    public MemcacheProperties memcacheProperties() {
        log.debug("Register MemcacheProperties");
        return new MemcacheProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshTokenStore refreshTokenStore(final RefreshProperties refreshProperties,
                                               final MemcacheProperties memcacheProperties,
                                               final UserDetailsService userDetailsService) {
        log.debug("Register MemcacheTokenStore");
        return new MemcacheTokenStore(refreshProperties, memcacheProperties, userDetailsService);
    }
}
