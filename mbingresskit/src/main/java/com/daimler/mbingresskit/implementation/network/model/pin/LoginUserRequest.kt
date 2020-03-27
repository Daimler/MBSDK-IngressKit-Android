package com.daimler.mbingresskit.implementation.network.model.pin

data class LoginUserRequest(
    val emailOrPhoneNumber: String,
    val countryCode: String,
    val locale: String
)