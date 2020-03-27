package com.daimler.mbingresskit.login.jwt

class DecodeJwtTokenException(token: String) : IllegalArgumentException("Failed to parse JWT-Token: $token")