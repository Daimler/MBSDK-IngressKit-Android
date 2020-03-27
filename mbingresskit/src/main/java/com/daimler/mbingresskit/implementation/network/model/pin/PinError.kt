package com.daimler.mbingresskit.implementation.network.model.pin

import com.google.gson.annotations.SerializedName

enum class PinError(val code: Int) {
    @SerializedName("2006")
    PIN_ALREADY_EXISTS(2006),
    @SerializedName("3015")
    CURRENT_PIN_TOO_SHORT(3015),
    @SerializedName("3016")
    CURRENT_PIN_TOO_LONG(3016),
    @SerializedName("3017")
    CURRENT_PIN_INVALID_FORMAT(3017),
    @SerializedName("3018")
    NEW_PIN_TOO_SHORT(3018),
    @SerializedName("3019")
    NEW_PIN_TOO_LONG(3019),
    @SerializedName("3020")
    NEW_PIN_INVALID_FORMAT(3020),
    @SerializedName("3021")
    CURRENT_PIN_INCORRECT(3021),
    UNKNOWN(-1)
}