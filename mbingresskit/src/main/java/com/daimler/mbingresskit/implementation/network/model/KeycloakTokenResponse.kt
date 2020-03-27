package com.daimler.mbingresskit.implementation.network.model

import com.google.gson.annotations.SerializedName

data class KeycloakTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Int,
    @SerializedName("scope") val scope: String,
    @SerializedName("typ") val typ: String
)