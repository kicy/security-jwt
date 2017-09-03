/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.it;

import eu.fraho.spring.securityJwt.it.spring.TestApiApplication;
import eu.fraho.spring.securityJwt.it.spring.UserDetailsServiceTestImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Getter
@Slf4j
@SpringBootTest(properties = "spring.config.location=classpath:test-mockrefresh.yaml",
        classes = TestApiApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAuthControllerMockRefresh extends AbstractTestAuthControllerWithRefresh {
    public TestAuthControllerMockRefresh() {
        super(new UserDetailsServiceTestImpl());
    }

    @Test
    @Override
    @Ignore
    public void testRefreshWithMultipleDeviceIds() {
        // not working with mock storage
    }
}
