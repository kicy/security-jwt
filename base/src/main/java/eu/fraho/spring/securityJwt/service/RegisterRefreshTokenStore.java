/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.service;

import eu.fraho.spring.securityJwt.config.RefreshProperties;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class RegisterRefreshTokenStore implements InitializingBean {
    public static final String BEAN_NAME = "refreshTokenStore";

    @Setter(onMethod = @__({@Autowired, @NonNull}))
    private ConfigurableListableBeanFactory factory;

    @Setter(onMethod = @__({@Autowired, @NonNull}))
    private RefreshProperties refreshProperties;

    @Setter(onMethod = @__({@NonNull}))
    private BeanDefinitionRegistry registry;

    public RegisterRefreshTokenStore(ConfigurableListableBeanFactory factory, RefreshProperties refreshProperties) {
        this.factory = factory;
        this.refreshProperties = refreshProperties;
    }

    @Override
    public void afterPropertiesSet() {
        if (registry == null) registry = ((BeanDefinitionRegistry) factory);

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(refreshProperties.getCacheImpl());
        beanDefinition.setLazyInit(false);
        beanDefinition.setAbstract(false);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope(AbstractBeanDefinition.SCOPE_DEFAULT);

        log.info("Registering RefreshTokenStore = {}", refreshProperties.getCacheImpl());
        registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
    }
}
