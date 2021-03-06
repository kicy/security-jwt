/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.it;

import eu.fraho.spring.securityJwt.it.spring.TestApiApplication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Getter
@Slf4j
@SpringBootTest(classes = TestApiApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthControllerTest extends AbstractAuthControllerNoRefreshTest {
}
