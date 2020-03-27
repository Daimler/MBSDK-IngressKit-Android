package com.daimler.mbingresskit.implementation.network.model.pin

import com.google.gson.annotations.SerializedName

data class SetPinRequest(@SerializedName("newPin") val newPin: String)