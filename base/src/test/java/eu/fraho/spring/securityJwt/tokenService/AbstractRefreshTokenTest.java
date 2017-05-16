package eu.fraho.spring.securityJwt.tokenService;

import eu.fraho.spring.securityJwt.dto.RefreshToken;
import eu.fraho.spring.securityJwt.service.JwtTokenServiceImplAccessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter(AccessLevel.NONE)
@Slf4j
public abstract class AbstractRefreshTokenTest extends JwtTokenServiceImplAccessor {
    @Before
    public void cleanupRefreshTokenMap() {
        jwtTokenService.clearTokens();
    }

    @Test
    public void testRefreshToken() throws InterruptedException {
        String jsmith = "jsmith";
        // first lease
        RefreshToken token = jwtTokenService.generateRefreshToken(jsmith);
        Assert.assertNotNull("No token generated", token);
        Assert.assertTrue("Token could not be used", jwtTokenService.useRefreshToken(jsmith, token));
        Assert.assertFalse("Token used twice", jwtTokenService.useRefreshToken(jsmith, token));
    }

    @Test
    public void testListRefreshTokensForUser() throws InterruptedException {
        String jsmith = "jsmith";
        String xsmith = "xsmith";

        RefreshToken tokenA = jwtTokenService.generateRefreshToken(jsmith);
        RefreshToken tokenB = jwtTokenService.generateRefreshToken(jsmith, "foobar");
        RefreshToken tokenC = jwtTokenService.generateRefreshToken(xsmith);

        final List<RefreshToken> jsmithTokens = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertTrue("Not all tokens returned", jsmithTokens.containsAll(Arrays.asList(tokenA, tokenB)));
        Assert.assertEquals("Unexpected token cound", 2, jsmithTokens.size());

        final List<RefreshToken> xsmithTokens = jwtTokenService.listRefreshTokens(xsmith);
        Assert.assertTrue("Not all tokens returned", xsmithTokens.contains(tokenC));
        Assert.assertEquals("Unexpected token cound", 1, xsmithTokens.size());
    }

    @Test
    public void testListRefreshTokens() throws InterruptedException {
        String jsmith = "jsmith";
        String xsmith = "xsmith";

        RefreshToken tokenA = jwtTokenService.generateRefreshToken(jsmith);
        RefreshToken tokenB = jwtTokenService.generateRefreshToken(jsmith, "foobar");
        RefreshToken tokenC = jwtTokenService.generateRefreshToken(xsmith);

        final Map<String, List<RefreshToken>> tokenMap = jwtTokenService.listRefreshTokens();
        Assert.assertEquals("User count don't match", 2, tokenMap.size());

        final List<RefreshToken> allTokens = tokenMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        Assert.assertEquals("Token count don't match", 3, allTokens.size());
        Assert.assertTrue("Not all tokens returned", allTokens.containsAll(Arrays.asList(tokenA, tokenB, tokenC)));
    }

    @Test(timeout = 10_000L)
    public void testListRefreshTokensExpiration() throws InterruptedException {
        String jsmith = "jsmith";
        String xsmith = "xsmith";

        jwtTokenService.generateRefreshToken(jsmith);
        jwtTokenService.generateRefreshToken(jsmith, "foobar");
        jwtTokenService.generateRefreshToken(xsmith);

        Thread.sleep(jwtTokenService.getRefreshExpiration() * 1000 + 100);

        final Map<String, List<RefreshToken>> tokenMap = jwtTokenService.listRefreshTokens();
        Assert.assertEquals("User count don't match", 0, tokenMap.size());

        final List<RefreshToken> allTokens = tokenMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
        Assert.assertEquals("Token count don't match", 0, allTokens.size());
    }

    @Test
    public void testRemoveSingleToken() throws InterruptedException {
        String jsmith = "jsmith";

        RefreshToken tokenA = jwtTokenService.generateRefreshToken(jsmith);
        RefreshToken tokenB = jwtTokenService.generateRefreshToken(jsmith, "foobar");

        final List<RefreshToken> tokens = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertEquals("Token count don't match", 2, tokens.size());

        jwtTokenService.revokeRefreshToken(jsmith, tokenA);
        Assert.assertEquals("Token list is not immutable", 2, tokens.size());
        final List<RefreshToken> tokens2 = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertEquals("Token was not revoked", 1, tokens2.size());
        Assert.assertTrue("Wrong token revoked", tokens2.contains(tokenB));
    }

    @Test
    public void testRemoveAllTokensForUser() throws InterruptedException {
        String jsmith = "jsmith";

        jwtTokenService.generateRefreshToken(jsmith);
        jwtTokenService.generateRefreshToken(jsmith, "foobar");

        final List<RefreshToken> tokens = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertEquals("Token count don't match", 2, tokens.size());

        jwtTokenService.revokeRefreshTokens(jsmith);
        Assert.assertEquals("Token list is not immutable", 2, tokens.size());
        final List<RefreshToken> tokens2 = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertTrue("Tokens were not revoked", tokens2.isEmpty());
    }

    @Test
    public void testRemoveTokenByUserAndDeviceId() throws InterruptedException {
        String jsmith = "jsmith";

        RefreshToken tokenA = jwtTokenService.generateRefreshToken(jsmith);
        RefreshToken tokenB = jwtTokenService.generateRefreshToken(jsmith, "foobar");

        final List<RefreshToken> tokens = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertEquals("Token count don't match", 2, tokens.size());

        jwtTokenService.revokeRefreshToken("jsmith", tokenB.getDeviceId());
        Assert.assertEquals("Token list is not immutable", 2, tokens.size());
        final List<RefreshToken> tokens2 = jwtTokenService.listRefreshTokens(jsmith);
        Assert.assertEquals("Token was not revoked", 1, tokens2.size());
        Assert.assertTrue("Wrong token revoked", tokens2.contains(tokenA));
    }

}