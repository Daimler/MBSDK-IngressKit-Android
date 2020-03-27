package com.daimler.mbingresskit.common

/**
 * This enumeration incorporates all the possible status code which can be received from CIAM.
 * The information are taken from the CIAM-Integration Guide, chapter 8.1.
 */
enum class StatusCode(val value: Int) {
    /**
     * Specifies that the error code is unknown or none was found
     */
    UNKNOWN(-1),

    /**
     * User has authenticated successfully.
     */
    AUTHENTICATION_SUCCESSFUL(0),
    /**
     * User has completed password reset successfully. User authentication session has been
    created if missing.
     */
    COMPLETED_PASSWORD_RESET(1),
    /**
     * Email address has been modified successfully. User authentication session has been
     * created if missing.
     */
    MODIFIED_EMAIL(2),
    /**
     * User has completed registration process successfully. User authentication session has
     * been created.
     */
    COMPLETED_REGISTRATION(3),
    /**
     * Mobile number has been modified successfully. User authentication session has been
    created if missing.
     */
    MODIFIED_MOBILE_NUMBER(5),
    /**
     * Authentication is not possible for the user at the moment. Proper error message has
     * been already provided to the user by WebLogin.
     */
    AUTHENTICATION_NOT_POSSIBLE(1001),
    /**
     * Password reset has been initiated, email sent successfully. User has been informed to
     * check the email.
     */
    FORGOT_PASSWORD(1002),
    /**
     * Registration confirmation email has been sent successfully. User has been informed to
     * check the email.
     */
    CREATED_ACCOUNT(1003),
    /**
     * Email change process has been initiated, email sent successfully. User has been
     * informed to check the email. User is most likely still authenticated however non-
     *  authenticated callback is chosen to avoid possible and unnecessary login requests in
     * case user kept open window without clicking on the "Close" button within an iframe
     * for too long (authentication session expiration).
     */
    EMAIL_SENT_SUCCESSFULLY(1004),
    /**
     * Registration, password-reset or email change token problem: The combination of email
     * and token is invalid, expired or has been already used. User has been informed to
     * restart the process.
     */
    REGISTRATION_PWD_RESET_EMAIL_CHANGE_TOKEN_PROBLEM(1005),
    /**
     * User has started authentication or registration but canceled it or denied social-network
     * authorization.
     */
    REGISTRATION_CANCELLED(2000),
    /**
     * User has been logged out.
     */
    USER_LOGOUT(2001),
    /**
     * User account has been completely removed. User authentication session was
     * destroyed. User ID of removed account is delivered via extra parameter "uid".
     * Client Application must invoke Backend Service to verify that the account has been removed
     * and only afterwards take necessary actions and display a "good bye" message.
     */
    ACCOUNT_REMOVED(2002),
    /**
     * User account has been unlinked from the Client Application but is still used by other
     * Client Applications. User authentication session was destroyed. User ID of removed
     * account is delivered via extra parameter "uid". Client Application must invoke Backend
     * Service to verify that the account has been removed and only afterwards take
     * necessary actions and display a "good bye" message.
     */
    ACCOUNT_UNLINKED(2003),
    /**
     * User authentication session is missing. This is used for the operations which are
     * available exclusively for the authenticated users and where the fallback to login is not
     * desired (e.g. Password Check operation).
     */
    USER_AUTHENTICATION_SESSION_MISSING(2004),
    /**
     * User data has been updated successfully.
     */
    UPDATED_USER_DATA(3000),
    /**
     * User password has been changed successfully.
     */
    PASSWORD_CHANGED(3001),
    /**
     * Password reset or set
     */
    PASSWORD_RESET_OR_SET(4002),
    /**
     * Confirm registration
     */
    CONFIRM_REGISTRATION(4003),
    /**
     * Confirm email address change
     */
    CONFIRM_EMAIL_CHANGED(4004),
    /**
     * CIAM is not fully available at the moment. No error message has been provided to the
     * user. The extra parameter "msg" is suitable for logging and trouble-shooting purposes
     * but should not be displayed to the user.
     */
    UNSPECIFIED_ERROR(9000);

    companion object {
        fun byValue(statusCode: Int): StatusCode {
            val code: StatusCode = if (statusCode >= UNSPECIFIED_ERROR.value) {
                UNSPECIFIED_ERROR
            } else {
                values().find { it.value == statusCode } ?: UNKNOWN
            }
            return code
        }
    }
}