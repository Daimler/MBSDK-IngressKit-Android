package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.login.error.*

sealed class OAuthLoginState : LoginState {

    abstract class BaseOAuthLoginState : OAuthLoginState() {
        override fun logout(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = LoggedOut
                finishLogout()
            }
        }
    }

    object LoggedOut : BaseOAuthLoginState() {
        override fun login(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = Authorizing
                authorize()
            }
        }

        override fun authorized(loginProcess: LoginProcess) {
            throw LoginNotStartedException("Client cannot be authorized before login was started.")
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            throw LoginNotStartedException("Token cannot be received before login was started.")
        }
    }

    object Authorizing : BaseOAuthLoginState() {
        override fun login(loginProcess: LoginProcess) {
            throw LoginAlreadyStartedException("Client authorization should be still pending and waiting to be authorized.")
        }

        override fun authorized(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = RequestingToken
                requestToken()
            }
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            throw ClientNotAuthorizedException("The client must be authorized before a token can be received.")
        }
    }

    object RequestingToken : BaseOAuthLoginState() {
        override fun login(loginProcess: LoginProcess) {
            throw LoginAlreadyStartedException("Token request should already be initialized.")
        }

        override fun authorized(loginProcess: LoginProcess) {
            throw ClientAlreadyAuthorized("Token request should already be initialized")
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = LoggedIn
                finishLogin()
            }
        }
    }

    object LoggedIn : BaseOAuthLoginState() {
        override fun login(loginProcess: LoginProcess) {
            throw ClientAlreadyLoggedIn("Cannot login again while already logged in")
        }

        override fun authorized(loginProcess: LoginProcess) {
            throw ClientAlreadyLoggedIn("Cannot be authorized again while already logged in")
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            throw ClientAlreadyLoggedIn("Cannot receive a token again while already logged in")
        }
    }
}