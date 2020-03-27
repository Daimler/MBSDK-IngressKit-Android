package com.daimler.mbingresskit;

import com.daimler.mbingresskit.common.AuthPrompt;
import com.daimler.mbingresskit.common.AuthScope;
import com.daimler.mbingresskit.ingress.CiamConfig;
import com.daimler.mbingresskit.ingress.CiamEnvironment;
import com.daimler.mbingresskit.ingress.EndpointCiamUrlFactory;
import com.daimler.mbingresskit.common.Prompt;
import com.daimler.mbingresskit.common.Scope;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CiamConfigTest {

    private EndpointCiamUrlFactory urlFactory = new EndpointCiamUrlFactory();

    @Test
    public void allAvailableUrlsFromEndpointUrlFactory() throws Exception {
        String[] availableUrlNames = urlFactory.getConfigNames();
        CiamEnvironment[] ciamEnvironments = CiamEnvironment.values();
        for (int index = 0; index < CiamEnvironment.values().length; index++) {
            String configName = availableUrlNames[index];
            assertEquals(ciamEnvironments[index].name(), configName);
            assertNotNull(urlFactory.createConfig(configName));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownCiamConfigName() throws Exception {
        urlFactory.createConfig("UNKNOWN");
    }

    @Test
    public void emptyTokenScope() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment);
        assertEquals("", config.getScope());
    }

    @Test
    public void oneTokenScope() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(Scope.OPEN_ID));
        assertEquals(Scope.OPEN_ID.getValue(), config.getScope());
    }

    @Test
    public void multipleTokenScope() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(Scope.OPEN_ID, Scope.EMAIL));
        assertEquals(Scope.OPEN_ID.getValue() + " " + Scope.EMAIL.getValue(), config.getScope());
    }

    @Test
    public void onlyAdditionalTokenScope() throws Exception {
        String customScope = "custom-scope";
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(customScope));
        assertEquals(customScope, config.getScope());
    }

    @Test
    public void additionalScopeAndTokenScope() throws Exception {
        String customScope = "cScope1 cScope2";
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(customScope, Scope.OPEN_ID, Scope.EMAIL));
        assertEquals(Scope.OPEN_ID.getValue() + " " + Scope.EMAIL.getValue() + " " + customScope, config.getScope());
    }

    @Test
    public void duplicateScope() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(Scope.CIAM, Scope.CIAM, Scope.EMAIL, Scope.EMAIL));
        assertEquals(Scope.CIAM.getValue() + " " + Scope.EMAIL.getValue(), config.getScope());
    }

    @Test
    public void emptyPrompt() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(), new AuthPrompt());
        assertEquals("", config.getPrompt());
    }

    @Test
    public void onePrompt() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(), new AuthPrompt(Prompt.LOGIN));
        assertEquals(Prompt.LOGIN.getValue(), config.getPrompt());
    }

    @Test
    public void multiplePrompt() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(), new AuthPrompt(Prompt.LOGIN, Prompt.CONSENT));
        assertEquals(Prompt.LOGIN.getValue() + " " + Prompt.CONSENT.getValue(), config.getPrompt());
    }

    @Test
    public void duplicatePrompt() throws Exception {
        CiamEnvironment ciamEnvironment = CiamEnvironment.INT;
        CiamConfig config = new CiamConfig(ciamEnvironment, ciamEnvironment, new AuthScope(), new AuthPrompt(Prompt.LOGIN, Prompt.LOGIN, Prompt.CONSENT, Prompt.CONSENT));
        assertEquals(Prompt.LOGIN.getValue() + " " + Prompt.CONSENT.getValue(), config.getPrompt());
    }
}
