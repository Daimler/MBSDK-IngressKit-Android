package com.daimler.mbingresskit.util

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.daimler.mbingresskit.common.JwtToken

fun String.toJwtToken(): JwtToken {
    val jwt: JWT
    try {
        jwt = JWT(this)
    } catch (e: DecodeException) {
        return JwtToken("", "")
    }
    return JwtToken(jwt.toString(), jwt.signature)
}