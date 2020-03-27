package com.daimler.mbingresskit.login

import com.daimler.mbnetworkkit.networking.RequestError

/**
 * Describes all the possible failures and/or interuption which can occur during the login process
 * which make it impossible to authorize the user.
 */
enum class LoginFailure : RequestError {
    /**
     * Signals that the user has cancelledByUser the login process by e.g. closing the browser window
     */
    CANCELLED_BY_USER,
    /**
     * Signals that a token exchange could not be performed
     */
    UNABLE_TO_EXCHANGE_TOKEN,
    /**
    * Signals that credentials are wrong
    **/
    WRONG_CREDENTIALS,
    /**
     * Signals that authorization failed
     **/
    AUTHORIZATION_FAILED,
    /**
     * Signals that the user has started the registration process
     */
    REGISTRATION_STARTED,
    /**
     * Signals that the user has the password reset process
     */
    PASSWORD_RESET
}