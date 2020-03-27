package com.daimler.mbingresskit;

import com.daimler.mbingresskit.ingress.CiamEnvironment;
import com.daimler.mbingresskit.ingress.EndpointKt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CiamEnvironmentTest {

    // All expected URLs are based on created url from CIAM-Example-App

    @Test
    public void authorizeINTurl() throws Exception {
        // based on created url from CIAM-Example-App
        String expectedIntAuthUrl = "https://api-test.secure.mercedes-benz.com/oidc10/auth/oauth/v2/authorize";
        assertEquals(expectedIntAuthUrl, EndpointKt.authEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void authorizePRODurl() throws Exception {
        String expectedIntAuthUrl = "https://api.secure.mercedes-benz.com/oidc10/auth/oauth/v2/authorize";
        assertEquals(expectedIntAuthUrl, EndpointKt.authEndpointUrl(CiamEnvironment.PROD));
    }

    @Test
    public void tokenINTurl() throws Exception {
        String expectedIntTokenUrl = "https://api-test.secure.mercedes-benz.com/oidc10/auth/oauth/v2/token";
        assertEquals(expectedIntTokenUrl, EndpointKt.tokenEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void tokenPRODurl() throws Exception {
        String expectedProdTokenUrl = "https://api.secure.mercedes-benz.com/oidc10/auth/oauth/v2/token";
        assertEquals(expectedProdTokenUrl, EndpointKt.tokenEndpointUrl(CiamEnvironment.PROD));
    }

    @Test
    public void registrationINTurl() throws Exception {
        String expectedIntRegistrationUrl = "https://login-test.secure.mercedes-benz.com/profile/register";
        assertEquals(expectedIntRegistrationUrl, EndpointKt.registrationEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void registrationPRODurl() throws Exception {
        String expectedProdRegistrationUrl = "https://login.secure.mercedes-benz.com/profile/register";
        assertEquals(expectedProdRegistrationUrl, EndpointKt.registrationEndpointUrl(CiamEnvironment.PROD));

    }

    @Test
    public void confirmRegistrationINTurl() throws Exception {
        String expectedIntConfirmRegistrationUrl = "https://login-test.secure.mercedes-benz.com/profile/confirm-registration";
        assertEquals(expectedIntConfirmRegistrationUrl, EndpointKt.confirmRegistrationEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void confirmRegistrationPRODurl() throws Exception {
        String expectedProdConfirmRegistrationUrl = "https://login.secure.mercedes-benz.com/profile/confirm-registration";
        assertEquals(expectedProdConfirmRegistrationUrl, EndpointKt.confirmRegistrationEndpointUrl(CiamEnvironment.PROD));
    }

    @Test
    public void passwordINTurl() throws Exception {
        String expectedIntPasswordUrl = "https://login-test.secure.mercedes-benz.com/profile/set-password";
        assertEquals(expectedIntPasswordUrl, EndpointKt.passwordEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void passwordPRODUrl() throws Exception {
        String expectedProdPasswordUrl = "https://login.secure.mercedes-benz.com/profile/set-password";
        assertEquals(expectedProdPasswordUrl, EndpointKt.passwordEndpointUrl(CiamEnvironment.PROD));
    }

    @Test
    public void logoutINTurl() throws Exception {
        String expectedIntLogoutUrl = "https://api-test.secure.mercedes-benz.com/ciam/logout";
        assertEquals(expectedIntLogoutUrl, EndpointKt.logoutEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void logoutPRODurl() throws Exception {
        String expectedProdLogoutUrl = "https://api.secure.mercedes-benz.com/ciam/logout";
        assertEquals(expectedProdLogoutUrl, EndpointKt.logoutEndpointUrl(CiamEnvironment.PROD));
    }

    @Test
    public void contentManagementINTurl() throws Exception {
        String expectedIntContentManagementUrl = "https://login-test.secure.mercedes-benz.com/profile/edit/apps";
        assertEquals(expectedIntContentManagementUrl, EndpointKt.contentManagementEndpointUrl(CiamEnvironment.INT));
    }

    @Test
    public void contentManagementPRODurl() throws Exception {
        String expectedProdContentManagementUrl = "https://login.secure.mercedes-benz.com/profile/edit/apps";
        assertEquals(expectedProdContentManagementUrl, EndpointKt.contentManagementEndpointUrl(CiamEnvironment.PROD));
    }


}