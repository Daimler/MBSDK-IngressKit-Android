package com.daimler.mbingresskit.implementation.network.model

import com.daimler.mbnetworkkit.networking.RequestError
import com.google.gson.annotations.SerializedName

internal data class ApiInputErrors(
    @SerializedName("errors") val errors: List<ApiInputError>?
) : RequestError