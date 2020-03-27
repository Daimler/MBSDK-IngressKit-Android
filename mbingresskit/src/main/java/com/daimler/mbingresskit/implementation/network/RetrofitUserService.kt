package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.AccountIdentifier
import com.daimler.mbingresskit.common.Address
import com.daimler.mbingresskit.common.CiamUserAgreement
import com.daimler.mbingresskit.common.CommunicationPreference
import com.daimler.mbingresskit.common.Countries
import com.daimler.mbingresskit.common.Country
import com.daimler.mbingresskit.common.CountryInstance
import com.daimler.mbingresskit.common.CountryLocale
import com.daimler.mbingresskit.common.CustomUserAgreement
import com.daimler.mbingresskit.common.CustomerDataField
import com.daimler.mbingresskit.common.LdssoUserAgreement
import com.daimler.mbingresskit.common.LoginUser
import com.daimler.mbingresskit.common.NatconUserAgreement
import com.daimler.mbingresskit.common.ProfileFieldDependency
import com.daimler.mbingresskit.common.ProfileFieldOwnerType
import com.daimler.mbingresskit.common.ProfileFieldRelationshipType
import com.daimler.mbingresskit.common.ProfileFieldUsage
import com.daimler.mbingresskit.common.ProfileFieldValidation
import com.daimler.mbingresskit.common.ProfileFieldsData
import com.daimler.mbingresskit.common.ProfileGroupDependency
import com.daimler.mbingresskit.common.ProfileSelectableValue
import com.daimler.mbingresskit.common.ProfileSelectableValues
import com.daimler.mbingresskit.common.RegistrationUser
import com.daimler.mbingresskit.common.SoeUserAgreement
import com.daimler.mbingresskit.common.UnitPreferences
import com.daimler.mbingresskit.common.User
import com.daimler.mbingresskit.common.UserAgreement
import com.daimler.mbingresskit.common.UserAgreementUpdates
import com.daimler.mbingresskit.common.UserAgreements
import com.daimler.mbingresskit.common.UserBiometricState
import com.daimler.mbingresskit.common.UserBodyHeight
import com.daimler.mbingresskit.common.UserInputError
import com.daimler.mbingresskit.common.UserInputErrors
import com.daimler.mbingresskit.common.UserPinStatus
import com.daimler.mbingresskit.implementation.mapProfileFieldResponseToProfileField
import com.daimler.mbingresskit.implementation.network.model.user.ApiAccountIdentifier
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementSubsystem
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementUpdate
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementUpdates
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.country.ApiCountryInstance
import com.daimler.mbingresskit.implementation.network.model.ApiInputErrors
import com.daimler.mbingresskit.implementation.network.model.adaptionvalues.ApiUserAdaptionValues
import com.daimler.mbingresskit.implementation.network.model.user.ApiVerifyUserRequest
import com.daimler.mbingresskit.implementation.network.model.pin.ChangePinRequest
import com.daimler.mbingresskit.implementation.network.model.country.CountryResponse
import com.daimler.mbingresskit.implementation.network.model.user.create.CreateUserRequest
import com.daimler.mbingresskit.implementation.network.model.user.create.CreateUserResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.CustomerDataFieldResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.FieldDependencyResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.FieldOwnerTypeResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.GroupDependencyResponse
import com.daimler.mbingresskit.implementation.network.model.pin.LoginUserRequest
import com.daimler.mbingresskit.implementation.network.model.pin.LoginUserResponse
import com.daimler.mbingresskit.implementation.network.model.pin.PinError
import com.daimler.mbingresskit.implementation.network.model.pin.PinErrorResponse
import com.daimler.mbingresskit.implementation.network.model.pin.PinErrors
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileDataFieldRelationshipTypeResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldTypeResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldUsageResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldValidationResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldsDataResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileSelectableValueResponse
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileSelectableValuesResponse
import com.daimler.mbingresskit.implementation.network.model.pin.SetPinRequest
import com.daimler.mbingresskit.implementation.network.model.user.update.UpdateUserRequest
import com.daimler.mbingresskit.implementation.network.model.user.update.UpdateUserRequestAddress
import com.daimler.mbingresskit.implementation.network.model.biometric.UserBiometricActivationStateRequest
import com.daimler.mbingresskit.implementation.network.model.biometric.UserBiometricApiState
import com.daimler.mbingresskit.implementation.network.model.user.UserCommunicationPreference
import com.daimler.mbingresskit.implementation.network.model.user.fetch.UserTokenResponse
import com.daimler.mbingresskit.implementation.network.model.unitpreferences.UserUnitPreferences
import com.daimler.mbingresskit.login.UserService
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.networking.RetrofitTask
import com.daimler.mbnetworkkit.networking.defaultErrorMapping
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.TaskObject
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.UUID

class RetrofitUserService(
    private val userApi: UserApi,
    private val sessionId: String,
    private val headerService: HeaderService
) : UserService {

    override fun sendPin(userName: String, countryCode: String): FutureTask<LoginUser, ResponseError<out RequestError>?> {
        val pinCall = userApi.sendPin(
            sessionId,
            UUID.randomUUID().toString(),
            LoginUserRequest(userName, countryCode, headerService.currentNetworkLocale()))
        val deferredTask = TaskObject<LoginUser, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<LoginUserResponse>()
        callback.onComplete {
            deferredTask.complete(LoginUser(it.userName, it.isEmail))
        }.onFailure {
            deferredTask.fail(requestPinErrorToResponseError(it))
        }
        pinCall.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun loadUser(jwtToken: String): FutureTask<User, ResponseError<out RequestError>?> {
        return loadUserFromApi(jwtToken, sessionId, UUID.randomUUID().toString())
    }

    override fun createUser(
        useMailAsUsername: Boolean,
        user: User
    ): FutureTask<RegistrationUser, ResponseError<out RequestError>?> {
        val call = userApi.createUser(
            sessionId,
            UUID.randomUUID().toString(),
            user.languageCode,
            CreateUserRequest(
                user.firstName,
                user.lastName,
                user.birthday.toNullIfBlank(),
                user.email.toNullIfBlank(),
                user.mobilePhone.toNullIfBlank(),
                user.landlinePhone.toNullIfBlank(),
                user.countryCode,
                user.languageCode,
                user.address?.let { mapAddressToAddressRequest(it) },
                if (user.title.isBlank()) null else user.title,
                user.salutationCode,
                user.taxNumber.toNullIfBlank(),
                mapCommunicationPreferenceToApiPreference(user.communicationPreference),
                useMailAsUsername
            )
        )
        val deferredTask = TaskObject<RegistrationUser, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<CreateUserResponse>()
        callback.futureTask()
            .onComplete {
                deferredTask.complete(
                    RegistrationUser(it.userId, it.firstName, it.lastName,
                        it.email, it.phone, it.password
                        ?: "", it.username, it.countryCode ?: "",
                        mapApiPreferenceToCommunicationPreference(it.communicationPreference))
                )
            }.onFailure { deferredTask.fail(mapDefaultInputError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun updateUser(jwtToken: String, user: User): FutureTask<User, ResponseError<out RequestError>?> {
        val call = userApi.updateUser(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            user.countryCode,
            UpdateUserRequest(
                user.firstName,
                user.lastName,
                null,
                user.birthday.toNullIfBlank(),
                user.email.toNullIfBlank(),
                user.mobilePhone.toNullIfBlank(),
                user.landlinePhone.toNullIfBlank(),
                user.countryCode,
                user.languageCode,
                user.address?.let { mapAddressToAddressRequest(it) },
                mapAccountIdentifierToApiAccountIdentifier(user.accountIdentifier),
                user.title,
                user.salutationCode,
                user.taxNumber,
                mapCommunicationPreferenceToApiPreference(user.communicationPreference)
            )
        )
        val deferredTask = TaskObject<User, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<UserTokenResponse>()
        callback.futureTask()
            .onComplete {
                deferredTask.complete(mapUserResponseToUser(it))
            }.onFailure { deferredTask.fail(mapDefaultInputError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun deleteUser(jwtToken: String, user: User): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.deleteUser(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            user.countryCode
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<ResponseBody>()
        callback.futureTask()
            .onComplete {
                deferredTask.complete(Unit)
            }.onFailure {
                deferredTask.fail(defaultErrorMapping(it))
            }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun updateProfilePicture(jwtToken: String, bitmapByteArray: ByteArray, mediaType: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val image = RequestBody.create(MediaType.parse(mediaType), bitmapByteArray)
        val call = userApi.updateProfilePicture(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            image
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<ResponseBody>()
        callback.futureTask()
            .onComplete { deferredTask.complete(Unit) }
            .onFailure { deferredTask.fail(defaultErrorMapping(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun fetchProfilePictureBytes(jwtToken: String): FutureTask<ByteArray, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<ByteArray, ResponseError<out RequestError>?>()

        fetchProfilePictureBytesInternal(
            jwtToken,
            deferredTask::complete,
            deferredTask::fail
        )

        return deferredTask.futureTask()
    }

    private fun fetchProfilePictureBytesInternal(
        jwtToken: String,
        completeHandler: (bytes: ByteArray) -> Unit,
        failureHandler: (error: ResponseError<out RequestError>?) -> Unit
    ) {

        val call = userApi.fetchProfilePicture(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString()
        )
        val callback = RetrofitTask<ResponseBody>()
        callback.futureTask()
            .onComplete { completeHandler(it.bytes()) }
            .onFailure { failureHandler(defaultErrorMapping(it)) }
        call.enqueue(callback)
    }

    override fun fetchCountries(): FutureTask<Countries, ResponseError<out RequestError>?> {
        val call = userApi.fetchCountries(
            sessionId,
            UUID.randomUUID().toString(),
            headerService.currentNetworkLocale()
        )
        val deferredTask = TaskObject<Countries, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<List<CountryResponse>>()
        callback.futureTask()
            .onComplete { deferredTask.complete(mapCountries(it)) }
            .onFailure { deferredTask.fail(defaultErrorMapping(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun fetchNatconCountries(): FutureTask<List<String>, ResponseError<out RequestError>?> {
        val call = userApi.fetchNatconCountries(
            sessionId,
            UUID.randomUUID().toString()
        )
        val deferredTask = TaskObject<List<String>, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<List<String>?>()
        callback.futureTask()
            .onComplete { deferredTask.complete(it ?: emptyList()) }
            .onFailure { deferredTask.fail(defaultErrorMapping(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun setPin(jwtToken: String, pin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.setPin(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            SetPinRequest(pin)
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = PinManipulatingRetrofitTask()
        callback.futureTask()
            .onComplete { deferredTask.complete(it) }
            .onFailure { deferredTask.fail(setPinErrorToResponseError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun changePin(jwtToken: String, currentPin: String, newPin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.changePin(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            ChangePinRequest(currentPin, newPin)
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = PinManipulatingRetrofitTask()
        callback.futureTask()
            .onComplete { deferredTask.complete(it) }
            .onFailure { deferredTask.fail(changePinErrorToResponseError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun deletePin(jwtToken: String, currentPin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.deletePin(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            currentPin
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = PinManipulatingRetrofitTask()
        callback.futureTask()
            .onComplete { deferredTask.complete(it) }
            .onFailure { deferredTask.fail(deletePinErrorToResponseError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun resetPin(jwtToken: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.resetPin(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString()
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = PinManipulatingRetrofitTask()
        callback.futureTask()
            .onComplete { deferredTask.complete(it) }
            .onFailure { deferredTask.fail(resetPinErrorToResponseError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun sendBiometricActivation(
        jwtToken: String,
        countryCode: String,
        state: UserBiometricState,
        currentPin: String?
    ): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.sendBiometricActivation(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            countryCode,
            UserBiometricActivationStateRequest(currentPin.orEmpty(),
                mapUserBiometricStateToApiState(state))
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = BiometricRetrofitTask()
        callback.futureTask()
            .onComplete { deferredTask.complete(it) }
            .onFailure { deferredTask.fail(mapSendBiometricError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun updateUnitPreferences(
        jwtToken: String,
        unitPreferences: UnitPreferences
    ): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.updateUnitPreferences(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            mapUnitPreferencesToApiPreferences(unitPreferences)
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = UpdateUnitPreferencesRetrofitTask()
        callback.futureTask()
            .onComplete {
                deferredTask.complete(Unit)
            }.onFailure {
                deferredTask.fail(defaultErrorMapping(it))
            }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun updateAdaptionValues(jwtToken: String, bodyHeight: UserBodyHeight): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.updateAdaptionValues(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            mapBodyHeightToApiAdaptionValues(bodyHeight)
        )
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = UpdateAdaptionValuesRetrofitTask()
        callback.futureTask()
            .onComplete {
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(mapDefaultInputError(it))
            }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun fetchProfileFields(countryCode: String, locale: String?): FutureTask<ProfileFieldsData, ResponseError<out RequestError>?> {
        val call = userApi.fetchProfileFields(
            sessionId,
            UUID.randomUUID().toString(),
            locale ?: headerService.currentNetworkLocale(),
            countryCode
        )
        val deferredTask = TaskObject<ProfileFieldsData, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<ProfileFieldsDataResponse>()
        callback
            .onComplete { deferredTask.complete(mapProfileFieldsDataResponseToProfileFieldsData(it)) }
            .onFailure { deferredTask.fail(mapFetchProfileFieldsError(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    override fun fetchCiamTermsAndConditions(
        countryCode: String,
        locale: String?
    ): FutureTask<UserAgreements<CiamUserAgreement>, ResponseError<out RequestError>?> {
        val callLocale = locale ?: headerService.currentNetworkLocale()
        val call = userApi.fetchCIAMTermsAndConditions(
            sessionId,
            UUID.randomUUID().toString(),
            callLocale,
            countryCode,
            callLocale,
            ApiAgreementSubsystem.CIAM
        )
        return loadApiAgreementsInternal(call) {
            CiamAgreementTask(callLocale, countryCode, userApi, it)
        }
    }

    override fun fetchSOETermsAndConditions(
        jwtToken: String,
        country: String,
        locale: String?
    ): FutureTask<UserAgreements<SoeUserAgreement>, ResponseError<out RequestError>?> {
        val callLocale = locale ?: headerService.currentNetworkLocale()
        val call = userApi.fetchSOETermsAndConditions(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            callLocale,
            country,
            callLocale,
            ApiAgreementSubsystem.SOE
        )
        return loadApiAgreementsInternal(call) {
            SoeAgreementTask(jwtToken, callLocale, country, userApi, it)
        }
    }

    override fun fetchCustomTermsAndConditions(
        countryCode: String,
        jwtToken: String?,
        locale: String?
    ): FutureTask<UserAgreements<CustomUserAgreement>, ResponseError<out RequestError>?> {
        val callLocale = locale ?: headerService.currentNetworkLocale()
        val call = userApi.fetchCustomTermsAndConditions(
            sessionId,
            UUID.randomUUID().toString(),
            callLocale,
            country = countryCode,
            locale = callLocale,
            subsystem = ApiAgreementSubsystem.CUSTOM
        )
        return loadApiAgreementsInternal(call) {
            CustomAgreementTask(callLocale, countryCode, userApi, it)
        }
    }

    override fun fetchLdssoTermsAndConditions(
        countryCode: String,
        jwtToken: String?,
        ldssoAppId: String,
        ldssoVersionId: String,
        locale: String?
    ): FutureTask<UserAgreements<LdssoUserAgreement>, ResponseError<out RequestError>?> {
        val callLocale = locale ?: headerService.currentNetworkLocale()
        val call = userApi.fetchLdssoTermsAndConditions(
            sessionId,
            UUID.randomUUID().toString(),
            ldssoAppId,
            ldssoVersionId,
            jwtToken,
            callLocale,
            country = countryCode,
            locale = callLocale,
            subsystem = ApiAgreementSubsystem.LDSSO
        )
        return loadApiAgreementsInternal(call) {
            LdssoAgreementTask(callLocale, countryCode, userApi, it)
        }
    }

    override fun fetchNatconTermsAndConditions(
        countryCode: String,
        locale: String?
    ): FutureTask<UserAgreements<NatconUserAgreement>, ResponseError<out RequestError>?> {
        val callLocale = locale ?: headerService.currentNetworkLocale()
        val call = userApi.fetchNatconTermsAndConditions(
            sessionId,
            UUID.randomUUID().toString(),
            callLocale,
            countryCode,
            callLocale,
            ApiAgreementSubsystem.NATCON
        )
        return loadApiAgreementsInternal(call) {
            NatconAgreementTask(callLocale, countryCode, userApi, it)
        }
    }

    override fun updateCiamAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        return updateAgreements(
            jwtToken,
            agreements.countryCode,
            ApiAgreementUpdates(
                ciam = mapUserAgreementUpdatesToApiAgreementUpdate(agreements)
            ),
            locale ?: headerService.currentNetworkLocale()
        )
    }

    override fun updateCustomAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        return updateAgreements(
            jwtToken,
            agreements.countryCode,
            ApiAgreementUpdates(
                custom = mapUserAgreementUpdatesToApiAgreementUpdate(agreements)
            ),
            locale ?: headerService.currentNetworkLocale()
        )
    }

    override fun updateLdssoAgreements(jwtToken: String, agreements: UserAgreementUpdates, ldssoAppId: String, ldssoVersionId: String, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        return updateAgreements(
            jwtToken,
            agreements.countryCode,
            ApiAgreementUpdates(
                ldsso = mapUserAgreementUpdatesToApiAgreementUpdate(agreements)
            ),
            locale ?: headerService.currentNetworkLocale(),
            ldssoAppId,
            ldssoVersionId
        )
    }

    override fun updateNatconAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        return updateAgreements(
            jwtToken,
            agreements.countryCode,
            ApiAgreementUpdates(
                natcon = mapUserAgreementUpdatesToApiAgreementUpdate(agreements)
            ),
            locale ?: headerService.currentNetworkLocale()
        )
    }

    override fun updateSOEAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        return updateAgreements(
            jwtToken,
            agreements.countryCode,
            ApiAgreementUpdates(
                soe = mapUserAgreementUpdatesToApiAgreementUpdate(agreements)
            ),
            locale ?: headerService.currentNetworkLocale()
        )
    }

    override fun verifyUser(jwtToken: String, scanReference: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.verifyUser(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            ApiVerifyUserRequest(scanReference))
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = RetrofitTask<ResponseBody>()
        callback.onComplete { deferredTask.complete(Unit) }
            .onFailure { deferredTask.fail(defaultErrorMapping(it)) }
        call.enqueue(callback)
        return deferredTask.futureTask()
    }

    private fun <T : UserAgreement> loadApiAgreementsInternal(
        call: Call<ApiAgreementsResponse>,
        taskCreator: (BaseAgreementTask.Callback<T>) -> BaseAgreementTask<*, T>
    ): FutureTask<UserAgreements<T>, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<UserAgreements<T>, ResponseError<out RequestError>?>()
        val callback = AgreementsRetrofitTask()

        val taskCallback = object : BaseAgreementTask.Callback<T> {
            override fun onComplete(agreements: UserAgreements<T>) {
                deferredTask.complete(agreements)
            }

            override fun onFailure(error: Throwable?) {
                deferredTask.fail(mapAgreementsError(error))
            }
        }
        val task = taskCreator(taskCallback)

        callback.futureTask()
            .onComplete { task.execute(it) }
            .onFailure { deferredTask.fail(mapAgreementsError(it)) }
        call.enqueue(callback)

        return deferredTask.futureTask()
    }

    private fun updateAgreements(
        jwtToken: String,
        countryCode: String,
        updates: ApiAgreementUpdates,
        locale: String,
        ldssoAppId: String? = null,
        ldssoVersionId: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?> {
        val call = userApi.updateAgreements(
            jwtToken,
            sessionId,
            UUID.randomUUID().toString(),
            ldssoAppId.orEmpty(),
            ldssoVersionId.orEmpty(),
            locale,
            countryCode,
            updates
        )
        val task = TaskObject<Unit, ResponseError<out RequestError>?>()
        val callback = UpdateUserAgreementsRetrofitTask()
        callback.futureTask()
            .onComplete { task.complete(it) }
            .onFailure { task.fail(mapUpdateAgreementsError(it)) }
        call.enqueue(callback)
        return task.futureTask()
    }

    private fun loadUserFromApi(jwtToken: String, sessionId: String, trackingId: String): FutureTask<User, ResponseError<out RequestError>?> {
        val userCall = userApi.fetchUserData(jwtToken, sessionId, trackingId)
        val userTask = TaskObject<User, ResponseError<out RequestError>?>()
        val userTaskCallback = RetrofitTask<UserTokenResponse>()
        userTaskCallback.futureTask()
            .onFailure {
                userTask.fail(defaultErrorMapping(it))
            }.onComplete {
                userTask.complete(mapUserResponseToUser(it))
            }
        userCall.enqueue(userTaskCallback)
        return userTask.futureTask()
    }

    private fun mapUserResponseToUser(response: UserTokenResponse) =
        User(
            response.ciamId,
            response.userId ?: "",
            response.firstName,
            response.lastName1,
            response.birthday.orEmpty(),
            response.email.orEmpty(),
            response.mobilePhone.orEmpty(),
            response.landlinePhone.orEmpty(),
            response.accountCountryCode.orEmpty(),
            response.preferredLanguageCode.orEmpty(),
            response.createdAt,
            response.updatedAt,
            mapUserPinStatus(response.userPinStatus),
            response.address?.let { mapApiAddressToAddress(it) },
            mapApiPreferenceToCommunicationPreference(response.communicationPreference),
            mapApiPreferencesToUnitPreferences(response.unitPreferences),
            mapApiAccountIdentifierToAccountIdentifier(response.accountIdentifier),
            response.title.orEmpty(),
            response.salutationCode.orEmpty(),
            response.taxNumber.orEmpty(),
            response.userAdaptionValues?.let { mapApiUserAdaptionValuesToBodyHeight(it) },
            response.accountVerified
        )

    private fun mapAddressToAddressRequest(address: Address) =
        UpdateUserRequestAddress(
            address.countryCode,
            address.state,
            address.province,
            address.street,
            address.houseNumber,
            address.zipCode,
            address.city,
            address.streetType,
            address.houseName,
            address.floorNumber,
            address.doorNumber,
            address.addressLine1,
            address.addressLine2,
            address.addressLine3,
            address.postOfficeBox
        )

    private fun mapApiAddressToAddress(address: UserTokenResponse.AddressResponse) =
        Address(
            address.street,
            address.houseNumber,
            address.zipCode,
            address.city,
            address.countryCode,
            address.state,
            address.province,
            address.streetType,
            address.houseName,
            address.floorNumber,
            address.doorNumber,
            address.addressLine1,
            address.addressLine2,
            address.addressLine3,
            address.postOfficeBox
        )

    private fun mapUserPinStatus(response: UserTokenResponse.UserPinStatusResponse?) =
        when (response) {
            UserTokenResponse.UserPinStatusResponse.SET -> UserPinStatus.SET
            UserTokenResponse.UserPinStatusResponse.NOT_SET -> UserPinStatus.NOT_SET
            UserTokenResponse.UserPinStatusResponse.UNKNOWN -> UserPinStatus.UNKNOWN
            else -> UserPinStatus.UNKNOWN
        }

    private fun mapAccountIdentifierToApiAccountIdentifier(identifier: AccountIdentifier) =
        when (identifier) {
            AccountIdentifier.EMAIL -> ApiAccountIdentifier.EMAIL
            AccountIdentifier.MOBILE -> ApiAccountIdentifier.MOBILE
            AccountIdentifier.EMAIL_AND_MOBILE -> ApiAccountIdentifier.EMAIL_AND_MOBILE
            AccountIdentifier.UNKNOWN -> null
        }

    private fun mapApiAccountIdentifierToAccountIdentifier(identifier: ApiAccountIdentifier?) =
        when (identifier) {
            ApiAccountIdentifier.EMAIL -> AccountIdentifier.EMAIL
            ApiAccountIdentifier.MOBILE -> AccountIdentifier.MOBILE
            ApiAccountIdentifier.EMAIL_AND_MOBILE -> AccountIdentifier.EMAIL_AND_MOBILE
            null -> AccountIdentifier.UNKNOWN
        }

    private fun mapCommunicationPreferenceToApiPreference(communicationPreference: CommunicationPreference) =
        UserCommunicationPreference(
            communicationPreference.contactByPhone,
            communicationPreference.contactByLetter,
            communicationPreference.contactByMail,
            communicationPreference.contactBySms
        )

    private fun mapApiPreferenceToCommunicationPreference(apiCommunicationPreference: UserCommunicationPreference?) =
        CommunicationPreference(
            apiCommunicationPreference?.contactByPhone ?: false,
            apiCommunicationPreference?.contactByLetter ?: false,
            apiCommunicationPreference?.contactByMail ?: false,
            apiCommunicationPreference?.contactBySms ?: false
        )

    private fun mapApiPreferencesToUnitPreferences(apiUnitPreferences: UserUnitPreferences?) =
        UnitPreferences(
            apiUnitPreferences?.clockHours ?: UnitPreferences.ClockHoursUnits.TYPE_24H,
            apiUnitPreferences?.speedDistance
                ?: UnitPreferences.SpeedDistanceUnits.KILOMETERS,
            apiUnitPreferences?.consumptionCo
                ?: UnitPreferences.ConsumptionCoUnits.LITERS_PER_100_KILOMETERS,
            apiUnitPreferences?.consumptionEv
                ?: UnitPreferences.ConsumptionEvUnits.KILOWATT_HOURS_PER_100_KILOMETERS,
            apiUnitPreferences?.consumptionGas
                ?: UnitPreferences.ConsumptionGasUnits.KILOGRAM_PER_100_KILOMETERS,
            apiUnitPreferences?.tirePressure
                ?: UnitPreferences.TirePressureUnits.KILOPASCAL,
            apiUnitPreferences?.temperature ?: UnitPreferences.TemperatureUnits.CELSIUS
        )

    private fun mapUnitPreferencesToApiPreferences(unitPreferences: UnitPreferences) =
        UserUnitPreferences(
            unitPreferences.clockHours,
            unitPreferences.speedDistance,
            unitPreferences.consumptionCo,
            unitPreferences.consumptionEv,
            unitPreferences.consumptionGas,
            unitPreferences.tirePressure,
            unitPreferences.temperature
        )

    private fun mapApiUserAdaptionValuesToBodyHeight(adaptionValues: ApiUserAdaptionValues) =
        UserBodyHeight(adaptionValues.bodyHeight, adaptionValues.preAdjustment)

    private fun mapBodyHeightToApiAdaptionValues(bodyHeight: UserBodyHeight) =
        ApiUserAdaptionValues(bodyHeight.bodyHeight, bodyHeight.preAdjustment)

    private fun mapProfileFieldsDataResponseToProfileFieldsData(data: ProfileFieldsDataResponse) =
        ProfileFieldsData(
            mapCustomerDataFieldsResponseToCustomerDataFields(data.customerDataFields),
            mapFieldDependenciesResponseToFieldDependencies(data.fieldDependencies),
            mapGroupDependenciesResponseToGroupDependencies(data.groupDependencies)
        )

    private fun mapCustomerDataFieldsResponseToCustomerDataFields(data: List<CustomerDataFieldResponse>?) =
        data?.map { mapCustomerDataFieldResponseToCustomerDataField(it) } ?: emptyList()

    private fun mapFieldDependenciesResponseToFieldDependencies(data: List<FieldDependencyResponse>?) =
        data?.map {
            ProfileFieldDependency(
                mapFieldOwnerTypeResponseToFieldOwnerType(it.fieldOwnerType),
                mapProfileFieldResponseToProfileField(it.itemId),
                mapFieldRelationshipTypeResponseToFieldRelationship(it.fieldType),
                mapProfileFieldsResponseToProfileFields(it.childrenIds)
            )
        } ?: emptyList()

    private fun mapGroupDependenciesResponseToGroupDependencies(data: List<GroupDependencyResponse>?) =
        data?.map {
            ProfileGroupDependency(
                mapProfileFieldResponseToProfileField(it.itemId),
                mapFieldRelationshipTypeResponseToFieldRelationship(it.fieldType),
                mapProfileFieldsResponseToProfileFields(it.childrenIds)
            )
        } ?: emptyList()

    private fun mapFieldOwnerTypeResponseToFieldOwnerType(data: FieldOwnerTypeResponse?) =
        when (data) {
            FieldOwnerTypeResponse.ACCOUNT -> ProfileFieldOwnerType.ACCOUNT
            FieldOwnerTypeResponse.VEHICLE -> ProfileFieldOwnerType.VEHICLE
            null -> ProfileFieldOwnerType.UNKNOWN
        }

    private fun mapFieldRelationshipTypeResponseToFieldRelationship(data: ProfileDataFieldRelationshipTypeResponse?) =
        when (data) {
            ProfileDataFieldRelationshipTypeResponse.GROUP -> ProfileFieldRelationshipType.GROUP
            ProfileDataFieldRelationshipTypeResponse.DATA_FIELD -> ProfileFieldRelationshipType.DATA_FIELD
            null -> ProfileFieldRelationshipType.UNKNOWN
        }

    private fun mapCustomerDataFieldResponseToCustomerDataField(data: CustomerDataFieldResponse) =
        CustomerDataField(
            mapProfileFieldResponseToProfileField(data.fieldType),
            data.sequenceOrder,
            mapProfileFieldUsageResponseToProfileFieldUsage(data.fieldUsageResponse),
            mapProfileFieldValidationResponseToProfile(data.fieldValidation),
            mapProfileValuesResponseToProfileSelectableValues(data.selectableValues)
        )

    private fun mapProfileFieldsResponseToProfileFields(data: List<ProfileFieldTypeResponse?>?) =
        data?.map { mapProfileFieldResponseToProfileField(it) } ?: emptyList()

    private fun mapProfileFieldUsageResponseToProfileFieldUsage(field: ProfileFieldUsageResponse?) =
        when (field) {
            ProfileFieldUsageResponse.OPTIONAL -> ProfileFieldUsage.OPTIONAL
            ProfileFieldUsageResponse.MANDATORY -> ProfileFieldUsage.MANDATORY
            ProfileFieldUsageResponse.INVISIBLE -> ProfileFieldUsage.INVISIBLE
            ProfileFieldUsageResponse.READ_ONLY -> ProfileFieldUsage.READ_ONLY
            null -> ProfileFieldUsage.UNKNOWN
        }

    private fun mapProfileFieldValidationResponseToProfile(data: ProfileFieldValidationResponse?) =
        data?.let {
            ProfileFieldValidation(
                it.minLength,
                it.maxLength,
                it.regularExpression
            )
        }

    private fun mapProfileValuesResponseToProfileSelectableValues(data: ProfileSelectableValuesResponse?) =
        data?.let {
            ProfileSelectableValues(
                it.matchSelectableValueByKey,
                it.defaultSelectableValueKey,
                mapProfileValueToProfileSelectableValue(it.selectableValues)
            )
        }

    private fun mapProfileValueToProfileSelectableValue(data: List<ProfileSelectableValueResponse>?) =
        data?.map { ProfileSelectableValue(it.key, it.description) } ?: emptyList()

    private fun mapUserAgreementUpdatesToApiAgreementUpdate(updates: UserAgreementUpdates) =
        updates.updates.map {
            ApiAgreementUpdate(
                it.userAgreementId, it.versionId, it.accepted, it.locale
            )
        }

    private fun requestPinErrorToResponseError(error: Throwable?): ResponseError<out RequestError> {
        return mapDefaultInputError(error)
    }

    private fun setPinErrorToResponseError(error: Throwable?): ResponseError<out RequestError> {
        return mapDefaultUserPinError(error)
    }

    private fun changePinErrorToResponseError(error: Throwable?): ResponseError<out RequestError> {
        return mapDefaultUserPinError(error)
    }

    private fun deletePinErrorToResponseError(error: Throwable?): ResponseError<out RequestError> {
        return mapDefaultInputError(error)
    }

    private fun resetPinErrorToResponseError(error: Throwable?): ResponseError<out RequestError> {
        return mapDefaultInputError(error)
    }

    private fun mapCountries(response: List<CountryResponse>): Countries {
        val countries =
            response.map {
                Country(
                    it.countryCode,
                    it.countryName,
                    mapApiCountryInstanceToCountryInstance(it.instance),
                    it.legalRegion,
                    it.defaultLocale,
                    it.natconCountry,
                    it.connectCountry,
                    it.locales?.map { locale -> CountryLocale(locale.localeCode, locale.localeName) },
                    it.availability
                )
            }
        return Countries(countries)
    }

    private fun mapApiCountryInstanceToCountryInstance(instance: ApiCountryInstance?) =
        when (instance) {
            ApiCountryInstance.ECE -> CountryInstance.ECE
            ApiCountryInstance.AMAP -> CountryInstance.AMAP
            ApiCountryInstance.CN -> CountryInstance.CN
            null -> CountryInstance.UNKNOWN
        }

    private fun mapUserBiometricStateToApiState(state: UserBiometricState) =
        when (state) {
            UserBiometricState.ENABLED -> UserBiometricApiState.ENABLED
            UserBiometricState.DISABLED -> UserBiometricApiState.DISABLED
        }

    private fun mapSendBiometricError(error: Throwable?) =
        defaultErrorMapping(error)

    private fun mapFetchProfileFieldsError(error: Throwable?) =
        defaultErrorMapping(error)

    private fun mapDefaultInputError(error: Throwable?): ResponseError<out RequestError> {
        val defaultError = defaultErrorMapping(error, ApiInputErrors::class.java)
        return if (defaultError.requestError is ApiInputErrors) {
            val inputErrors = defaultError.requestError as ApiInputErrors
            return ResponseError.requestError(mapApiInputErrorsToInputErrors(inputErrors))
        } else {
            defaultError
        }
    }

    private fun mapDefaultUserPinError(error: Throwable?): ResponseError<out RequestError> {
        val defaultError = defaultErrorMapping(error, PinErrorResponse::class.java)
        return if (defaultError.requestError is PinErrorResponse) {
            val pinErrors = defaultError.requestError as PinErrorResponse
            ResponseError.requestError(PinErrors(pinErrors.code.map {
                it ?: PinError.UNKNOWN
            }))
        } else {
            defaultError
        }
    }

    private fun mapApiInputErrorsToInputErrors(errors: ApiInputErrors) =
        UserInputErrors(
            errors.errors?.map {
                UserInputError(it.fieldName, it.description)
            } ?: emptyList()
        )

    private fun mapAgreementsError(error: Throwable?) =
        defaultErrorMapping(error)

    private fun mapUpdateAgreementsError(error: Throwable?) =
        defaultErrorMapping(error)

    private fun String.toNullIfBlank() = if (isBlank()) null else this
}