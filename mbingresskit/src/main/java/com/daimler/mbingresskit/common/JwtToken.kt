package com.daimler.mbingresskit.common

data class JwtToken internal constructor(
    val plainToken: String,
    val payload: String
)
