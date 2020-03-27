package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.login.error.ClientAlreadyLoggedIn
import com.daimler.mbingresskit.login.error.LoginAlreadyStartedException
import com.daimler.mbingresskit.login.error.LoginNotStartedException

sealed class UserCredentialsLoginState : LoginState {
    abstract class BaseUserCredentialsLoginState : UserCredentialsLoginState() {
        override fun authorized(loginProcess: LoginProcess) {
            throw UnsupportedOperationException("Should not be called because ${UserCredentialsLoginState::class.java.simpleName} not requires authorization.")
        }

        override fun logout(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = LoggedOut
                finishLogout()
            }
        }
    }

    object LoggedOut : BaseUserCredentialsLoginState() {
        override fun login(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = RequestingToken
                requestToken()
            }
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            throw LoginNotStartedException("Token cannot be received before login was started.")
        }
    }

    object RequestingToken : BaseUserCredentialsLoginState() {
        override fun login(loginProcess: LoginProcess) {
            throw LoginAlreadyStartedException("Token request should already be initialized.")
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            loginProcess.apply {
                loginState = LoggedIn
                finishLogin()
            }
        }
    }

    object LoggedIn : BaseUserCredentialsLoginState() {
        override fun login(loginProcess: LoginProcess) {
            throw ClientAlreadyLoggedIn("Cannot login again while already logged in")
        }

        override fun tokenReceived(loginProcess: LoginProcess) {
            throw ClientAlreadyLoggedIn("Cannot receive a token again while already logged in")
        }
    }
}