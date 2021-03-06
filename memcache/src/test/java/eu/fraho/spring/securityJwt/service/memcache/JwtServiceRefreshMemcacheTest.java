/*
 * MIT Licence
 * Copyright (c) 2017 Simon Frankenberger
 *
 * Please see LICENCE.md for complete licence text.
 */
package eu.fraho.spring.securityJwt.service.memcache;

import eu.fraho.spring.securityJwt.config.RefreshProperties;
import eu.fraho.spring.securityJwt.dto.JwtUser;
import eu.fraho.spring.securityJwt.dto.RefreshToken;
import eu.fraho.spring.securityJwt.dto.TimeWithPeriod;
import eu.fraho.spring.securityJwt.memcache.config.MemcacheProperties;
import eu.fraho.spring.securityJwt.memcache.service.MemcacheTokenStore;
import eu.fraho.spring.securityJwt.service.JwtTokenService;
import eu.fraho.spring.securityJwt.service.RefreshTokenStore;
import eu.fraho.spring.securityJwt.ut.service.AbstractJwtTokenServiceWithRefreshTest;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
public class JwtServiceRefreshMemcacheTest extends AbstractJwtTokenServiceWithRefreshTest {
    private final RefreshProperties refreshProperties;
    private final MemcacheTokenStore refreshTokenStore;

    public JwtServiceRefreshMemcacheTest() throws Exception {
        refreshProperties = getRefreshProperties();
        refreshTokenStore = new MemcacheTokenStore();
        refreshTokenStore.setRefreshProperties(refreshProperties);
        refreshTokenStore.setMemcacheProperties(new MemcacheProperties());
        refreshTokenStore.setUserDetailsService(getUserdetailsService());

        refreshTokenStore.afterPropertiesSet();
    }

    @Override
    @NotNull
    protected RefreshTokenStore getRefreshStore() {
        return refreshTokenStore;
    }

    @Test(timeout = 10_000L)
    public void testListRefreshTokensOtherEntries() throws Exception {
        JwtTokenService service = getService();

        JwtUser jsmith = getJwtUser();
        jsmith.setUsername("jsmith");
        JwtUser xsmith = getJwtUser();
        xsmith.setUsername("xsmith");

        RefreshToken tokenA = service.generateRefreshToken(jsmith);
        RefreshToken tokenB = service.generateRefreshToken(jsmith);
        RefreshToken tokenC = service.generateRefreshToken(xsmith);

        MemcachedClient client = getMemcachedClient();
        client.set("foobar", 30, "hi").get();

        final Map<Long, List<RefreshToken>> tokenMap = service.listRefreshTokens();
        Assert.assertEquals("User count don't match", 2, tokenMap.size());

        final List<RefreshToken> allTokens = tokenMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        Assert.assertEquals("AbstractToken count don't match", 3, allTokens.size());
        Assert.assertTrue("Not all tokens returned", allTokens.containsAll(Arrays.asList(tokenA, tokenB, tokenC)));
    }

    private MemcachedClient getMemcachedClient() throws Exception {
        Field memcachedClient = MemcacheTokenStore.class.getDeclaredField("memcachedClient");
        memcachedClient.setAccessible(true);
        return (MemcachedClient) memcachedClient.get(refreshTokenStore);
    }

    @Test(expected = IllegalStateException.class)
    public void testExpirationBounds() throws Exception {
        Field expiration = RefreshProperties.class.getDeclaredField("expiration");
        expiration.setAccessible(true);
        Object oldValue = expiration.get(refreshProperties);
        try {
            expiration.set(refreshProperties, new TimeWithPeriod(31, TimeUnit.DAYS));
            refreshTokenStore.afterPropertiesSet();
        } finally {
            expiration.set(refreshProperties, oldValue);
        }
    }
}
