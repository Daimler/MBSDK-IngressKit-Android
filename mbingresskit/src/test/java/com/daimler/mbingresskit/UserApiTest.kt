package com.daimler.mbingresskit

import com.daimler.mbingresskit.common.UnitPreferences
import com.daimler.mbingresskit.implementation.network.UserApi
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAcceptedByUser
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementSubsystem
import com.daimler.mbingresskit.implementation.network.model.biometric.UserBiometricActivationStateRequest
import com.daimler.mbingresskit.implementation.network.model.pin.ChangePinRequest
import com.daimler.mbingresskit.implementation.network.model.pin.LoginUserRequest
import com.daimler.mbingresskit.implementation.network.model.pin.SetPinRequest
import com.daimler.mbingresskit.implementation.network.model.profilefields.FieldOwnerTypeResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileDataFieldRelationshipTypeResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldUsageResponse
import com.daimler.mbingresskit.implementation.network.model.user.ApiAccountIdentifier
import com.daimler.mbingresskit.implementation.network.model.user.create.CreateUserRequest
import com.daimler.mbingresskit.implementation.network.model.user.fetch.UserTokenResponse
import com.daimler.mbingresskit.implementation.network.model.user.update.UpdateUserRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.SoftAssertions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class UserApiTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var userApi: UserApi

    @BeforeEach
    fun setUp() {
        mockServer = MockWebServer()
        userApi = Retrofit.Builder()
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockServer.url("/"))
            .build()
            .create(UserApi::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun `send pin call with valid data`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, LOGIN_JSON_FILE))

        val loginUserResponse = userApi.sendPin(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            body = LoginUserRequest("johnneumann@example.com", COUNTRY_CODE, "de-DE")
        ).execute().body()

        assertEquals(true, loginUserResponse?.isEmail)
        assertEquals("johnneumann", loginUserResponse?.userName)
    }

    @ParameterizedTest
    @ValueSource(ints = [HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_INTERNAL_ERROR,
        HttpURLConnection.HTTP_BAD_GATEWAY, HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_NOT_FOUND])
    fun `send pin call with different http response`(responseCode: Int) {
        mockServer.enqueue(createMockResponse(responseCode))

        val loginUserResponse = userApi.sendPin(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            body = LoginUserRequest("thisisnotanemail", "asdf", "asdf-ASDF")
        ).execute().body()

        assertEquals(null, loginUserResponse)
    }

    @Test
    fun `fetch user data`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, USER_JSON_FILE))

        val userTokenResponse = userApi.fetchUserData(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID
        ).execute().body()

        val softly = SoftAssertions()
        softly.assertThat(userTokenResponse).isNotNull
        softly.assertThat(userTokenResponse?.ciamId).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.firstName).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.lastName1).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.lastName2).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.birthday).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.email).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.mobilePhone).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.landlinePhone).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.accountCountryCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.preferredLanguageCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.createdAt).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.updatedAt).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.title).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.salutationCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.taxNumber).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.accountVerified).isEqualTo(true)
        // Address
        softly.assertThat(userTokenResponse?.address).isNotNull
        softly.assertThat(userTokenResponse?.address?.countryCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.state).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.province).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.street).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.houseNumber).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.zipCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.city).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.streetType).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.houseName).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.floorNumber).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.doorNumber).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.addressLine1).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.addressLine2).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.addressLine3).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(userTokenResponse?.address?.postOfficeBox).isEqualTo(JSON_FIELD_VALUE)
        // UserPinStatus
        assertEquals(UserTokenResponse.UserPinStatusResponse.SET, userTokenResponse?.userPinStatus)
        // UserCommunicationPreference
        softly.assertThat(userTokenResponse?.communicationPreference).isNotNull
        softly.assertThat(userTokenResponse?.communicationPreference?.contactByPhone).isEqualTo(true)
        softly.assertThat(userTokenResponse?.communicationPreference?.contactByLetter).isEqualTo(true)
        softly.assertThat(userTokenResponse?.communicationPreference?.contactByMail).isEqualTo(true)
        softly.assertThat(userTokenResponse?.communicationPreference?.contactBySms).isEqualTo(true)
        // UserUnitPreferences
        softly.assertThat(userTokenResponse?.unitPreferences).isNotNull
        softly.assertThat(userTokenResponse?.unitPreferences?.clockHours).isEqualTo(UnitPreferences.ClockHoursUnits.TYPE_24H)
        softly.assertThat(userTokenResponse?.unitPreferences?.speedDistance).isEqualTo(UnitPreferences.SpeedDistanceUnits.KILOMETERS)
        softly.assertThat(userTokenResponse?.unitPreferences?.consumptionCo).isEqualTo(UnitPreferences.ConsumptionCoUnits.LITERS_PER_100_KILOMETERS)
        softly.assertThat(userTokenResponse?.unitPreferences?.consumptionEv).isEqualTo(UnitPreferences.ConsumptionEvUnits.KILOWATT_HOURS_PER_100_KILOMETERS)
        softly.assertThat(userTokenResponse?.unitPreferences?.consumptionGas).isEqualTo(UnitPreferences.ConsumptionGasUnits.KILOGRAM_PER_100_KILOMETERS)
        softly.assertThat(userTokenResponse?.unitPreferences?.tirePressure).isEqualTo(UnitPreferences.TirePressureUnits.KILOPASCAL)
        softly.assertThat(userTokenResponse?.unitPreferences?.temperature).isEqualTo(UnitPreferences.TemperatureUnits.CELSIUS)
        // ApiAccountIdentifier
        softly.assertThat(userTokenResponse?.accountIdentifier).isEqualTo(ApiAccountIdentifier.EMAIL)

        softly.assertAll()
    }

    @ParameterizedTest
    @ValueSource(ints = [HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_INTERNAL_ERROR,
        HttpURLConnection.HTTP_BAD_GATEWAY, HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_NOT_FOUND])
    fun `fetch user data with different http response`(responseCode: Int) {
        mockServer.enqueue(createMockResponse(responseCode))

        val userTokenResponse = userApi.fetchUserData(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID
        ).execute().body()

        assertEquals(null, userTokenResponse)
    }

    @Test
    fun `create user data`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, CREATE_USER_JSON_FILE))

        val createUserRequest = mock(CreateUserRequest::class.java)
        val createUserResponse = userApi.createUser(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            locale = HEADER_LOCALE,
            body = createUserRequest
        ).execute().body()

        val softly = SoftAssertions()
        softly.assertThat(createUserResponse?.email).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.firstName).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.lastName).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.username).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.userId).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.phone).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.countryCode).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(createUserResponse?.communicationPreference).isEqualTo(null)

        softly.assertAll()
    }

    @ParameterizedTest
    @ValueSource(ints = [HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_INTERNAL_ERROR,
        HttpURLConnection.HTTP_BAD_GATEWAY, HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_NOT_FOUND])
    fun `create user data with different http response`(responseCode: Int) {
        mockServer.enqueue(createMockResponse(responseCode))

        val createUserRequest = mock(CreateUserRequest::class.java)
        val createUserResponse = userApi.createUser(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            locale = HEADER_LOCALE,
            body = createUserRequest
        ).execute().body()

        assertEquals(null, createUserResponse)
    }

    @Test
    fun `update user data`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, UPDATE_USER_JSON_FILE))

        val updateUserRequest = mock(UpdateUserRequest::class.java)
        val userTokenResponse = userApi.updateUser(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            countryCode = COUNTRY_CODE,
            body = updateUserRequest
        ).execute().body()

        assertEquals(TIMESTAMP_EXAMPLE, userTokenResponse?.updatedAt)
    }

    @ParameterizedTest
    @ValueSource(ints = [HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_INTERNAL_ERROR,
        HttpURLConnection.HTTP_BAD_GATEWAY, HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_NOT_FOUND])
    fun `update user data with different http response`(responseCode: Int) {
        mockServer.enqueue(createMockResponse(responseCode))

        val updateUserRequest = mock(UpdateUserRequest::class.java)
        val userTokenResponse = userApi.updateUser(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            countryCode = COUNTRY_CODE,
            body = updateUserRequest
        ).execute().body()

        assertEquals(null, userTokenResponse)
    }

    @Test
    fun `delete user data`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val responseBody = userApi.deleteUser(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            countryCode = COUNTRY_CODE
        ).execute().body()
        assertNotNull(responseBody)
    }

    @Test
    fun `fetch profile picture`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val responseBody = userApi.fetchProfilePicture(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID
        ).execute().body()
        assertNotNull(responseBody)
    }

    @Test
    fun `update profile picture`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val responseBody = userApi.updateProfilePicture(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            image = RequestBody.create(MEDIA_TYPE.toMediaTypeOrNull(), ByteArray(1234))
        ).execute().body()
        assertNotNull(responseBody)
    }

    @Test
    fun `fetch countries`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, COUNTRIES_JSON_FILE))

        val responseBody = userApi.fetchCountries(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            locale = HEADER_LOCALE
        ).execute().body()

        assertNotNull(responseBody)
        val countryResponse = responseBody?.first()
        assertEquals(JSON_FIELD_VALUE, countryResponse?.countryCode)
        assertEquals(JSON_FIELD_VALUE, countryResponse?.countryName)
        assertEquals(null, countryResponse?.instance)
        assertEquals(JSON_FIELD_VALUE, countryResponse?.legalRegion)
        assertEquals(true, countryResponse?.connectCountry)
        assertEquals(true, countryResponse?.natconCountry)
        assertEquals(JSON_FIELD_VALUE, countryResponse?.locales?.first()?.localeCode)
    }

    @Test
    fun `set pin`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val setPinRequest = mock(SetPinRequest::class.java)
        val responseBody = userApi.setPin(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            pinRequest = setPinRequest
        ).execute().body()

        assertNotNull(responseBody)
    }

    @Test
    fun `change pin`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val changePinRequest = mock(ChangePinRequest::class.java)
        val responseBody = userApi.changePin(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            pinRequest = changePinRequest
        ).execute().body()

        assertNotNull(responseBody)
    }

    @Test
    fun `delete pin`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val responseBody = userApi.deletePin(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            currentPin = "12345"
        ).execute().body()

        assertNotNull(responseBody)
    }

    @Test
    fun `send biometric activation`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK))

        val userBiometricActivationStateRequest = mock(UserBiometricActivationStateRequest::class.java)
        val sendBiometricActivationResponse = userApi.sendBiometricActivation(
            jwtToken = JWT_TOKEN,
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            countryCode = COUNTRY_CODE,
            body = userBiometricActivationStateRequest
        )

        assertNotNull(sendBiometricActivationResponse)
    }

    @Test
    fun `fetch profile fields`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, PROFILE_JSON_FILE))

        val profileFieldsDataResponse = userApi.fetchProfileFields(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            headerLocale = HEADER_LOCALE,
            countryCode = COUNTRY_CODE
        ).execute().body()

        val softly = SoftAssertions()
        val customerDataFieldResponse = profileFieldsDataResponse?.customerDataFields?.first()
        softly.assertThat(customerDataFieldResponse).isNotNull
        softly.assertThat(customerDataFieldResponse?.sequenceOrder).isEqualTo(0)
        softly.assertThat(customerDataFieldResponse?.fieldUsageResponse).isEqualTo(ProfileFieldUsageResponse.INVISIBLE)
        val fieldValidation = customerDataFieldResponse?.fieldValidation
        softly.assertThat(fieldValidation?.minLength).isEqualTo(0)
        softly.assertThat(fieldValidation?.maxLength).isEqualTo(0)
        softly.assertThat(fieldValidation?.regularExpression).isEqualTo(JSON_FIELD_VALUE)
        val selectableValues = customerDataFieldResponse?.selectableValues
        softly.assertThat(selectableValues?.matchSelectableValueByKey).isEqualTo(true)
        softly.assertThat(selectableValues?.defaultSelectableValueKey).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(selectableValues?.selectableValues?.first()?.key).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(selectableValues?.selectableValues?.first()?.description).isEqualTo(JSON_FIELD_VALUE)

        val groupDependencyResponse = profileFieldsDataResponse?.groupDependencies?.first()
        softly.assertThat(groupDependencyResponse?.fieldType).isEqualTo(ProfileDataFieldRelationshipTypeResponse.GROUP)

        val fieldDependencyResponse = profileFieldsDataResponse?.fieldDependencies?.first()
        softly.assertThat(fieldDependencyResponse?.fieldOwnerType).isEqualTo(FieldOwnerTypeResponse.ACCOUNT)
        softly.assertThat(fieldDependencyResponse?.fieldType).isEqualTo(ProfileDataFieldRelationshipTypeResponse.GROUP)

        softly.assertAll()
    }

    @Test
    fun `fetch terms and conditions`() {
        mockServer.enqueue(createMockResponse(HttpURLConnection.HTTP_OK, TERMS_AND_CONDITIONS_JSON_FILE))

        val apiAgreementSubsystem = mock(ApiAgreementSubsystem::class.java)
        val apiAgreementResponse = userApi.fetchCIAMTermsAndConditions(
            sessionId = SESSION_ID,
            trackingId = TRACKING_ID,
            headerLocale = HEADER_LOCALE,
            country = COUNTRY_CODE,
            locale = HEADER_LOCALE,
            subsystem = apiAgreementSubsystem
        ).execute().body()

        val softly = SoftAssertions()
        softly.assertThat(apiAgreementResponse?.errors?.first()?.error).isEqualTo(JSON_FIELD_VALUE)
        val apiSoeAgreement = apiAgreementResponse?.soe?.first()
        softly.assertThat(apiSoeAgreement?.documentId).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiSoeAgreement?.documentVersion).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiSoeAgreement?.position).isEqualTo(0)
        softly.assertThat(apiSoeAgreement?.displayName).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiSoeAgreement?.isGeneralUserAgreement).isEqualTo(true)
        softly.assertThat(apiSoeAgreement?.checkBoxText).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiSoeAgreement?.titleText).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiSoeAgreement?.acceptedByUser).isEqualTo(ApiAcceptedByUser.ACCEPTED)
        val apiCiamAgreement = apiAgreementResponse?.ciam?.first()
        softly.assertThat(apiCiamAgreement?.documentId).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiCiamAgreement?.documentVersion).isEqualTo(JSON_FIELD_VALUE)
        val apiCustomAgreement = apiAgreementResponse?.custom?.first()
        softly.assertThat(apiCustomAgreement?.appId).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiCustomAgreement?.category).isEqualTo(JSON_FIELD_VALUE)
        // TODO not same as Swagger YAML. Reason has to be found
        // assertEquals("0", apiCustomAgreement?.version)
        softly.assertThat(apiCustomAgreement?.displayLocation).isEqualTo(JSON_FIELD_VALUE)
        val apiNatconAgreement = apiAgreementResponse?.natcon?.first()
        softly.assertThat(apiNatconAgreement?.version).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiNatconAgreement?.title).isEqualTo(JSON_FIELD_VALUE)
        softly.assertThat(apiNatconAgreement?.isMandatory).isEqualTo(true)
        // TODO not same as Swagger YAML. Reason has to be found
        // assertEquals(ApiAcceptedByUser.ACCEPTED, apiNatconAgreement?.acceptedByUser)

        softly.assertAll()
    }

    private fun createMockResponse(responseCode: Int, jsonFilePath: String? = null) = MockResponse().apply {
        setResponseCode(responseCode)
        jsonFilePath?.let {
            setBody(UserApiTest::class.java.getResource(it).readText())
        }
    }

    companion object {
        private const val JSON_FIELD_VALUE = "string"
        private const val SESSION_ID = "MySessionId"
        private const val JWT_TOKEN = "MyJwtToken"
        private const val TRACKING_ID = "MyTrackingId"
        private const val HEADER_LOCALE = "de"
        private const val COUNTRY_CODE = "DE"
        private const val TIMESTAMP_EXAMPLE = "2020-03-18T15:31:33.148Z"
        private const val MEDIA_TYPE = "image/jpeg"

        // JSON Files
        private const val LOGIN_JSON_FILE = "/login_200.json"
        private const val USER_JSON_FILE = "/get_user_200.json"
        private const val CREATE_USER_JSON_FILE = "/post_user_200.json"
        private const val UPDATE_USER_JSON_FILE = "/put_user_200.json"
        private const val COUNTRIES_JSON_FILE = "/get_countries_200.json"
        private const val PROFILE_JSON_FILE = "/get_profile_fields_200.json"
        private const val TERMS_AND_CONDITIONS_JSON_FILE = "/get_terms_and_conditions_200.json"
    }
}