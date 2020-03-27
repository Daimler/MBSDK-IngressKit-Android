package com.daimler.mbingresskit.login.jwt

import com.daimler.mbingresskit.common.JwtToken

interface JwtTokenValidator {
    fun isValidToken(jwtToken: JwtToken): Boolean
    fun isExpired(jwtToken: JwtToken): Boolean
}