package com.daimler.mbingresskit.common

data class UserAgreements<T : UserAgreement>(
    val locale: String,
    val countryCode: String,
    val agreements: List<T>
)