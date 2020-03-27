package com.daimler.mbingresskit.login.jwt

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.JwtToken

class JWTDecodeTokenValidator : JwtTokenValidator {

    override fun isValidToken(jwtToken: JwtToken): Boolean {
        return try {
            JWT(jwtToken.plainToken)
            MBLoggerKit.d("JwtToken is valid: ${jwtToken.plainToken}")
            true
        } catch (e: DecodeException) {
            MBLoggerKit.d("JwtToken is invalid: ${jwtToken.plainToken}")
            false
        }
    }

    override fun isExpired(jwtToken: JwtToken): Boolean {
        return try {
            val jwt = JWT(jwtToken.plainToken)
            val isExpired = jwt.isExpired(0)
            MBLoggerKit.d("JwtToken is expired = $isExpired (expires at: ${jwt.expiresAt})")
            isExpired
        } catch (e: DecodeException) {
            false
        }
    }
}