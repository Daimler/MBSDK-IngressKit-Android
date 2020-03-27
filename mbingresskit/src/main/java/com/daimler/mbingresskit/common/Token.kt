package com.daimler.mbingresskit.common

data class Token @JvmOverloads constructor(
    val typ: String,
    val accessToken: String,
    val refreshToken: String,
    val jwtToken: JwtToken,
    val tokenExpirationDate: Long,
    val refreshTokenExpirationDate: Long,
    val scope: String = ""
)
