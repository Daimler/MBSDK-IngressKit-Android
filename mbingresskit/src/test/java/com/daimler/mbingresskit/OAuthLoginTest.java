package com.daimler.mbingresskit;

import com.daimler.mbingresskit.login.LoginActionHandler;
import com.daimler.mbingresskit.login.LoginProcess;
import com.daimler.mbingresskit.login.OAuthLoginState;
import com.daimler.mbingresskit.login.error.ClientAlreadyAuthorized;
import com.daimler.mbingresskit.login.error.ClientAlreadyLoggedIn;
import com.daimler.mbingresskit.login.error.ClientNotAuthorizedException;
import com.daimler.mbingresskit.login.error.LoginAlreadyStartedException;
import com.daimler.mbingresskit.login.error.LoginNotStartedException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OAuthLoginTest implements LoginActionHandler {

    private static final String ACTION_AUTHORIZE = "A";
    private static final String ACTION_REQUEST_TOKEN = "R";
    private static final String ACTION_FINISH_LOGIN = "F";
    private static final String ACTION_LOGOUT = "O";

    private LoginProcess loginProcess;

    private String actions = "";

    @Before
    public void setup() {
        loginProcess = new LoginProcess(this, OAuthLoginState.LoggedOut.INSTANCE);
        actions = "";
    }

    private void assertActions(String expected) {
        assertEquals(expected, actions);
    }

    @Test
    public void startLogin() throws Exception {
        loginProcess.login();
        assertActions(ACTION_AUTHORIZE);
    }

    @Test
    public void authorizeAndRequestToken() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        assertActions(ACTION_AUTHORIZE + ACTION_REQUEST_TOKEN);
    }

    @Test
    public void fullLogin() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.tokenReceived();
        assertActions(ACTION_AUTHORIZE + ACTION_REQUEST_TOKEN + ACTION_FINISH_LOGIN);
    }

    @Test
    public void fullLoginAndLogout() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.tokenReceived();
        loginProcess.logout();
        assertActions(ACTION_AUTHORIZE + ACTION_REQUEST_TOKEN + ACTION_FINISH_LOGIN + ACTION_LOGOUT);
    }

    @Test
    public void logoutAfterLoginStartedProgress() throws Exception {
        loginProcess.login();
        loginProcess.logout();
        assertActions(ACTION_AUTHORIZE + ACTION_LOGOUT);
    }

    @Test
    public void logoutAfterAuthorized() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.logout();
        assertActions(ACTION_AUTHORIZE + ACTION_REQUEST_TOKEN + ACTION_LOGOUT);
    }

    @Test(expected = LoginNotStartedException.class)
    public void authorizedBeforeLoginStarted() throws Exception {
        loginProcess.authorized();
    }

    @Test(expected = LoginNotStartedException.class)
    public void tokenReceivedBeforeLoginStarted() throws Exception {
        loginProcess.tokenReceived();
    }

    @Test(expected = LoginAlreadyStartedException.class)
    public void loginStartedWhileWaitingForAuthorization() throws Exception {
        loginProcess.login();
        loginProcess.login();
    }

    @Test(expected = ClientNotAuthorizedException.class)
    public void tokenReceivedBeforeAuthorized() throws Exception {
        loginProcess.login();
        loginProcess.tokenReceived();
    }

    @Test(expected = LoginAlreadyStartedException.class)
    public void loginStartedWhileWaitingForToken() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.login();
    }

    @Test(expected = ClientAlreadyAuthorized.class)
    public void authorizeWhileWaitingForToken() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.authorized();
    }

    @Test(expected = ClientAlreadyLoggedIn.class)
    public void loginWhileAlreadyLoggedIn() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.tokenReceived();
        loginProcess.login();
    }

    @Test(expected = ClientAlreadyLoggedIn.class)
    public void authorizedWhileAlreadyLoggedIn() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.tokenReceived();
        loginProcess.authorized();
    }

    @Test(expected = ClientAlreadyLoggedIn.class)
    public void tokenReceivedWhileAlreadyLoggedIn() throws Exception {
        loginProcess.login();
        loginProcess.authorized();
        loginProcess.tokenReceived();
        loginProcess.tokenReceived();
    }

    @Override
    public void authorize() {
        actions += ACTION_AUTHORIZE;
    }

    @Override
    public void requestToken() {
        actions += ACTION_REQUEST_TOKEN;
    }

    @Override
    public void finishLogin() {
        actions += ACTION_FINISH_LOGIN;
    }

    @Override
    public void finishLogout() {
        actions += ACTION_LOGOUT;
    }

}