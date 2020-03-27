package com.daimler.mbingresskit.implementation.network.model.pin

import com.daimler.mbnetworkkit.networking.RequestError
import com.google.gson.annotations.SerializedName

data class PinErrorResponse(@SerializedName("code") val code: List<PinError?>) : RequestError