package com.daimler.mbingresskit.ingress

import java.net.URL

/**
 * Defines a  CIAM configuration which is sufficient for a full authentication cycle.
 */
interface Endpoint {
        /* Base url provided by CIAM (@see https://team.sp.wp.corpintra.net/sites/05389/CIAM/CIAM%20Documents/CIAM-Integration-Guide-OIDC.pdf) */
        val openIdBaseUrl: String
        /* Login base url provided by CIAM (@see https://team.sp.wp.corpintra.net/sites/05389/CIAM/CIAM%20Documents/CIAM-Integration-Guide-OIDC.pdf) */
        val webLoginBaseUrl: String
        /* Redirect url (Can be found in the CIAM claim) */
        val redirectUrl: String
}

fun Endpoint.authEndpointUrl() = URL(URL(this.openIdBaseUrl), CiamPaths.AUTHORIZE.pathValue).toString()

fun Endpoint.tokenEndpointUrl() = URL(URL(this.openIdBaseUrl), CiamPaths.TOKEN.pathValue).toString()

fun Endpoint.registrationEndpointUrl() = URL(URL(this.webLoginBaseUrl), CiamPaths.REGISTRATION.pathValue).toString()

fun Endpoint.confirmRegistrationEndpointUrl() = URL(URL(this.webLoginBaseUrl), CiamPaths.CONFIRMREGISTRATION.pathValue).toString()

fun Endpoint.passwordEndpointUrl() = URL(URL(this.webLoginBaseUrl), CiamPaths.PASSWORD.pathValue).toString()

fun Endpoint.logoutEndpointUrl() = URL(URL(this.openIdBaseUrl), CiamPaths.LOGOUT.pathValue).toString()

fun Endpoint.contentManagementEndpointUrl() = URL(URL(this.webLoginBaseUrl), CiamPaths.CONTENTMANAGEMENT.pathValue).toString()

fun Endpoint.demoCallbackEndpointUrl() = URL(URL(this.redirectUrl), CiamPaths.NATIVE_CALLBACK.pathValue).toString()

fun Endpoint.demoRedirectEndpointUrl() = URL(URL(this.redirectUrl), CiamPaths.NATIVE_REDIRECT.pathValue).toString()

fun Endpoint.demoLogoutUrl() = URL(URL(this.redirectUrl), CiamPaths.NATIVE_LOGOUT.pathValue).toString()
