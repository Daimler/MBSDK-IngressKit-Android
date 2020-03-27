package com.daimler.mbingresskit.implementation.network.model.agreement

internal data class RawAgreementsResponse(
    val response: ApiAgreementsResponse,
    val complete: Boolean
)