/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.starter;

import eu.fraho.spring.securityJwt.JwtAuthenticationEntryPoint;
import eu.fraho.spring.securityJwt.config.*;
import eu.fraho.spring.securityJwt.controller.AuthenticationRestController;
import eu.fraho.spring.securityJwt.dto.JwtUser;
import eu.fraho.spring.securityJwt.service.JwtTokenService;
import eu.fraho.spring.securityJwt.service.JwtTokenServiceImpl;
import eu.fraho.spring.securityJwt.service.TotpService;
import eu.fraho.spring.securityJwt.service.TotpServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class SecurityJwtBaseAutoConfiguration {
    @Bean
    public TotpProperties totpProperties() {
        log.debug("Register TotpProperties");
        return new TotpProperties();
    }

    @Bean
    public TotpService totpService() {
        log.debug("Register TotpService");
        TotpServiceImpl totpService = new TotpServiceImpl();
        totpService.setTotpProperties(totpProperties());
        return totpService;
    }

    @Bean
    public TokenProperties tokenProperties() {
        log.debug("Register TokenProperties");
        return new TokenProperties();
    }

    @Bean
    public RefreshProperties refreshProperties() {
        log.debug("Register RefreshProperties");
        return new RefreshProperties();
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        log.debug("Register JwtTokenService");
        JwtTokenServiceImpl jwtTokenService = new JwtTokenServiceImpl();
        jwtTokenService.setTokenProperties(tokenProperties());
        jwtTokenService.setRefreshProperties(refreshProperties());
        jwtTokenService.setTokenCookieProperties(tokenCookieProperties());
        jwtTokenService.setTokenHeaderProperties(tokenHeaderProperties());
        jwtTokenService.setRefreshCookieProperties(refreshCookieProperties());
        jwtTokenService.setJwtUser(this::jwtUser);
        return jwtTokenService;
    }

    @Bean
    public TokenCookieProperties tokenCookieProperties() {
        log.debug("Register TokenCookieProperties");
        return new TokenCookieProperties();
    }

    @Bean
    public TokenHeaderProperties tokenHeaderProperties() {
        log.debug("Register TokenHeaderProperties");
        return new TokenHeaderProperties();
    }

    @Bean
    public RefreshCookieProperties refreshCookieProperties() {
        log.debug("Register RefreshCookieProperties");
        return new RefreshCookieProperties();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        log.debug("Register JwtAuthenticationEntryPoint");
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        log.debug("Register StandardPasswordEncoder");
        return new StandardPasswordEncoder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public JwtUser jwtUser() {
        log.debug("Register JwtUser");
        return new JwtUser();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsService defaultUserDetailsService() {
        log.debug("Register EmptyUserDetailsService");
        return new EmptyUserDetailsService();
    }

    @Bean
    public AuthenticationRestController authenticationRestController(final AuthenticationManager authenticationManager,
                                                                     final JwtTokenService jwtTokenService,
                                                                     final UserDetailsService userDetailsService,
                                                                     final TotpService totpService,
                                                                     final TokenProperties tokenProperties,
                                                                     final RefreshProperties refreshProperties) {
        log.debug("Register AuthenticationRestController");
        AuthenticationRestController controller = new AuthenticationRestController();
        controller.setAuthenticationManager(authenticationManager);
        controller.setJwtTokenService(jwtTokenService);
        controller.setUserDetailsService(userDetailsService);
        controller.setTotpService(totpService);
        controller.setTokenProperties(tokenProperties);
        controller.setRefreshProperties(refreshProperties);
        return controller;
    }

    @Bean
    public JwtSecurityConfig webSecurityConfig(final UserDetailsService userDetailsService,
                                               final PasswordEncoder passwordEncoder,
                                               final JwtTokenService jwtTokenService,
                                               final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        log.debug("Register JwtSecurityConfig");
        JwtSecurityConfig config = new JwtSecurityConfig();
        config.setUserDetailsService(userDetailsService);
        config.setPasswordEncoder(passwordEncoder);
        config.setJwtTokenService(jwtTokenService);
        config.setJwtAuthenticationEntryPoint(jwtAuthenticationEntryPoint);
        return config;
    }
}
