package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.implementation.network.model.biometric.UserBiometricActivationStateRequest
import com.daimler.mbingresskit.implementation.network.model.country.CountryResponse
import com.daimler.mbingresskit.implementation.network.model.pin.ChangePinRequest
import com.daimler.mbingresskit.implementation.network.model.pin.LoginUserRequest
import com.daimler.mbingresskit.implementation.network.model.pin.LoginUserResponse
import com.daimler.mbingresskit.implementation.network.model.pin.SetPinRequest
import com.daimler.mbingresskit.implementation.network.model.adaptionvalues.ApiUserAdaptionValues
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementSubsystem
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementUpdates
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiHtmlAgreement
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiUpdateUserAgreements
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldsDataResponse
import com.daimler.mbingresskit.implementation.network.model.user.fetch.UserTokenResponse
import com.daimler.mbingresskit.implementation.network.model.unitpreferences.UserUnitPreferences
import com.daimler.mbingresskit.implementation.network.model.user.ApiVerifyUserRequest
import com.daimler.mbingresskit.implementation.network.model.user.create.CreateUserRequest
import com.daimler.mbingresskit.implementation.network.model.user.create.CreateUserResponse
import com.daimler.mbingresskit.implementation.network.model.user.update.UpdateUserRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {

    companion object {
        private const val PATH_VERSION = "/v1"
        private const val PATH_USERS = "/user"
        private const val PATH_COUNTRIES = "/countries"
        private const val PATH_LOGIN = "/login"
        private const val PATH_PIN = "/pin"
        private const val PATH_RESET = "/reset"
        private const val PATH_BIOMETRIC = "/biometric"
        private const val PATH_PROFILEPICTURE = "/profilepicture"
        private const val PATH_UNIT_PREFERENCES = "/unitpreferences"
        private const val PATH_ADAPTION_VALUES = "/adaptionValues"
        private const val PATH_PROFILE = "/profile"
        private const val PATH_FIELDS = "/fields"
        private const val PATH_VERIFICATION = "/verification"
        private const val PATH_SELF = "self"
        private const val PATH_AGREEMENTS = "/agreements"
        private const val PATH_NATCON = "/natcon"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_SESSION_ID = "X-SessionId"
        private const val HEADER_TRACKING_ID = "X-TrackingId"
        private const val HEADER_COUNTRY_CODE = "X-MarketCountryCode"
        private const val HEADER_LOCALE = "X-Locale"
        private const val HEADER_LDSSO_APP_ID = "ldsso-AppId"
        private const val HEADER_LDSSO_APP_VERSION = "ldsso-AppVersion"
        private const val QUERY_LOCALE = "locale"
        private const val QUERY_COUNTRY_CODE = "countryCode"
        private const val QUERY_CURRENT_PIN = "currentPin"
        private const val QUERY_COUNTRY = "addressCountry"
        private const val QUERY_SUBSYSTEM = "subsystem"
    }

    @POST("$PATH_VERSION$PATH_LOGIN")
    fun sendPin(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body body: LoginUserRequest
    ): Call<LoginUserResponse>

    @POST("$PATH_VERSION$PATH_USERS/$PATH_SELF$PATH_PIN")
    fun setPin(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body pinRequest: SetPinRequest
    ): Call<ResponseBody>

    @PUT("$PATH_VERSION$PATH_USERS/$PATH_SELF$PATH_PIN")
    fun changePin(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body pinRequest: ChangePinRequest
    ): Call<ResponseBody>

    @DELETE("$PATH_VERSION$PATH_USERS/$PATH_SELF$PATH_PIN")
    fun deletePin(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Query(QUERY_CURRENT_PIN) currentPin: String
    ): Call<ResponseBody>

    @POST("$PATH_VERSION$PATH_USERS$PATH_PIN$PATH_RESET")
    fun resetPin(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String
    ): Call<ResponseBody>

    @GET("$PATH_VERSION$PATH_USERS/$PATH_SELF")
    fun fetchUserData(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String
    ): Call<UserTokenResponse>

    @POST("$PATH_VERSION$PATH_USERS")
    fun createUser(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) locale: String,
        @Body body: CreateUserRequest
    ): Call<CreateUserResponse>

    @PUT("$PATH_VERSION$PATH_USERS/$PATH_SELF")
    fun updateUser(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_COUNTRY_CODE) countryCode: String,
        @Body body: UpdateUserRequest
    ): Call<UserTokenResponse>

    @DELETE("$PATH_VERSION$PATH_USERS/$PATH_SELF")
    fun deleteUser(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_COUNTRY_CODE) countryCode: String
    ): Call<ResponseBody>

    @PUT("$PATH_VERSION$PATH_USERS/$PATH_SELF$PATH_PROFILEPICTURE")
    fun updateProfilePicture(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body image: RequestBody
    ): Call<ResponseBody>

    @GET("$PATH_VERSION$PATH_USERS/$PATH_SELF$PATH_PROFILEPICTURE")
    fun fetchProfilePicture(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String
    ): Call<ResponseBody>

    @GET("$PATH_VERSION$PATH_COUNTRIES")
    fun fetchCountries(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Query(QUERY_LOCALE) locale: String
    ): Call<List<CountryResponse>>

    @GET("$PATH_VERSION$PATH_COUNTRIES$PATH_NATCON")
    fun fetchNatconCountries(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String
    ): Call<List<String>?>

    @POST("$PATH_VERSION$PATH_USERS$PATH_BIOMETRIC")
    fun sendBiometricActivation(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_COUNTRY_CODE) countryCode: String,
        @Body body: UserBiometricActivationStateRequest
    ): Call<ResponseBody>

    @PUT("$PATH_VERSION$PATH_USERS$PATH_UNIT_PREFERENCES")
    fun updateUnitPreferences(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body body: UserUnitPreferences
    ): Call<ResponseBody>

    @PUT("$PATH_VERSION$PATH_USERS$PATH_ADAPTION_VALUES")
    fun updateAdaptionValues(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body body: ApiUserAdaptionValues
    ): Call<ResponseBody>

    @GET("$PATH_VERSION$PATH_PROFILE$PATH_FIELDS")
    fun fetchProfileFields(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(QUERY_COUNTRY_CODE) countryCode: String
    ): Call<ProfileFieldsDataResponse>

    /*
    Agreements
     */

    @POST("$PATH_VERSION$PATH_USERS$PATH_AGREEMENTS")
    fun updateAgreements(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LDSSO_APP_ID) ldssoAppId: String,
        @Header(HEADER_LDSSO_APP_VERSION) ldssoAppVersion: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(QUERY_COUNTRY) countryCode: String,
        @Body body: ApiAgreementUpdates
    ): Call<ResponseBody>

    /*
    CIAM
     */

    @GET("$PATH_VERSION$PATH_AGREEMENTS")
    fun fetchCIAMTermsAndConditions(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(value = QUERY_COUNTRY, encoded = true) country: String,
        @Query(value = QUERY_LOCALE, encoded = true) locale: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem
    ): Call<ApiAgreementsResponse>

    @GET
    fun fetchHtmlAgreementsContent(@Url url: String): Call<ApiHtmlAgreement>

    /*
    SOE
     */
    @GET("$PATH_VERSION$PATH_AGREEMENTS")
    fun fetchSOETermsAndConditions(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(value = QUERY_COUNTRY, encoded = true) country: String,
        @Query(value = QUERY_LOCALE, encoded = true) locale: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem
    ): Call<ApiAgreementsResponse>

    @GET
    fun fetchPdfAgreementsContent(@Url fileUrl: String): Call<ResponseBody>

    @POST("$PATH_VERSION$PATH_USERS$PATH_AGREEMENTS")
    fun updateSOETermsAndConditions(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Query(value = QUERY_COUNTRY, encoded = true) countryCode: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem,
        @Body body: ApiUpdateUserAgreements
    ): Call<ResponseBody>

    /*
    NATCON
     */
    @GET("$PATH_VERSION$PATH_AGREEMENTS")
    fun fetchNatconTermsAndConditions(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(value = QUERY_COUNTRY, encoded = true) country: String,
        @Query(value = QUERY_LOCALE, encoded = true) locale: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem
    ): Call<ApiAgreementsResponse>

    /*
   CUSTOM
    */
    @GET("$PATH_VERSION$PATH_AGREEMENTS")
    fun fetchCustomTermsAndConditions(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(value = QUERY_COUNTRY, encoded = true) country: String,
        @Query(value = QUERY_LOCALE, encoded = true) locale: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem
    ): Call<ApiAgreementsResponse>

    /*
    LDSSO
     */
    @GET("$PATH_VERSION$PATH_AGREEMENTS")
    fun fetchLdssoTermsAndConditions(
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Header(HEADER_LDSSO_APP_ID) ldssoAppId: String,
        @Header(HEADER_LDSSO_APP_VERSION) ldssoAppVersion: String,
        @Header(HEADER_AUTHORIZATION) jwtToken: String?,
        @Header(HEADER_LOCALE) headerLocale: String,
        @Query(value = QUERY_COUNTRY, encoded = true) country: String,
        @Query(value = QUERY_LOCALE, encoded = true) locale: String,
        @Query(value = QUERY_SUBSYSTEM, encoded = true) subsystem: ApiAgreementSubsystem
    ): Call<ApiAgreementsResponse>

    @POST("$PATH_VERSION$PATH_USERS$PATH_VERIFICATION")
    fun verifyUser(
        @Header(HEADER_AUTHORIZATION) jwtToken: String,
        @Header(HEADER_SESSION_ID) sessionId: String,
        @Header(HEADER_TRACKING_ID) trackingId: String,
        @Body body: ApiVerifyUserRequest
    ): Call<ResponseBody>
}