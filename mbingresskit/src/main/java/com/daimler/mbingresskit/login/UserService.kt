package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.*
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask

interface UserService {

    /**
     * Sends a pin for the passed username, which is required to perform a successful login.
     *
     * @param userName The value that should be passed depends on the registration process.
     *                          This could be the users mail or phone.
     *
     * @return If the requests completes, the pin is send to the user by mail or sms.
     *                          If the request fails, a [NetworkError] or [HttpError] is passed.
     */
    fun sendPin(userName: String, countryCode: String): FutureTask<LoginUser, ResponseError<out RequestError>?>

    /**
     * Loads the current user data.
     */
    fun loadUser(jwtToken: String): FutureTask<User, ResponseError<out RequestError>?>

    /**
     * Creates the user with passed data. After registration was successfull, the user can now simply
     * login by sending a pin. For registration, only mail or phone should be set.
     *
     *
     * @param useMailAsUsername A flag indicating whether [User.email] shall be used as username, otherwise [User.mobilePhone] will be used.
     *
     * @param user The user to create.
     *
     * @return If registration was completed, the registered user will be returned in completion
     *                          block. If the registration fails, the failure block will be called. If there
     *                          was no network available, a [NetworkError] will be passed in Failure block.
     *                          If response contains 400 as Response-Code, a [RegistrationErrors] object
     *                          is passed. A [HttpError] is passed, if non of the previous errors occurred.
     */
    fun createUser(
        useMailAsUsername: Boolean,
        user: User
    ): FutureTask<RegistrationUser, ResponseError<out RequestError>?>

    /**
     * Updates the remote user object with the data of the given user object.
     *
     * @param jwtToken the current user token
     * @param user the user object to put
     * @return A [FutureTask] that contains the updated [User] object on its completion block
     * and one of the following objects in its failure block if the request failed.
     *  - [NetworkError]
     *  - [HttpError]
     *  - [UserInputErrors]
     */
    fun updateUser(jwtToken: String, user: User): FutureTask<User, ResponseError<out RequestError>?>

    /**
     * Deletes the related user. After completed, the user must be logged out because its token
     * will not be valid anymore.
     */
    fun deleteUser(jwtToken: String, user: User): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates user profile pic
     *
     * @param bitmapByteArray byte array of Bitmap to send
     *
     * @param mediaType possible values "image/jpeg", "image/jpg" and "image/png"
     *
     */
    fun updateProfilePicture(jwtToken: String, bitmapByteArray: ByteArray, mediaType: String): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Returns user profile pic as a byte array
     */
    fun fetchProfilePictureBytes(jwtToken: String): FutureTask<ByteArray, ResponseError<out RequestError>?>

    /**
     * Loads a list of all supported countries.
     */
    fun fetchCountries(): FutureTask<Countries, ResponseError<out RequestError>?>

    /**
     * Loads a list of country codes of all countries that require national consent.
     */
    fun fetchNatconCountries(): FutureTask<List<String>, ResponseError<out RequestError>?>

    /**
     * Sets the initial pin code for the user.
     *
     * @param jwtToken the current token of the user
     * @param pin the pin that should be set
     * @return A [FutureTask] that contains nothing in its completion block and one of the
     * following objects in its failure block, if the request failed:
     *  - [NetworkError]
     *  - [UserInputErrors] if the response code was 400
     *  - [HttpError] in any other case
     */
    fun setPin(jwtToken: String, pin: String): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Changes the current user pin.
     *
     * @param jwtToken the current token of the user
     * @param currentPin the current pin of the user
     * @param newPin the new pin that should be set as the user pin
     * @return A [FutureTask] that contains nothing in its completion block and one of the
     * following objects in its failure block, if the request failed:
     *  - [NetworkError]
     *  - [UserInputErrors] if the response code was 400
     *  - [HttpError] in any other case
     */
    fun changePin(jwtToken: String, currentPin: String, newPin: String): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Deletes the current user pin.
     *
     * @param jwtToken the current token of the user
     * @param currentPin the current user pin
     * @return A [FutureTask] that contains nothing in its completion block and one of the
     * following objects in its failure block, if the request failed:
     *  - [NetworkError]
     *  - [UserInputErrors] if the response code was 400
     *  - [HttpError] in any other case
     */
    fun deletePin(jwtToken: String, currentPin: String): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Resets the current user pin.
     *
     * @param jwtToken the current token of the user
     * @return A [FutureTask] that contains nothing in its completion block and one of the
     * following objects in its failure block, if the request failed:
     *  - [NetworkError]
     *  - [UserInputErrors] if the response code was 400
     *  - [HttpError] in any other case
     */
    fun resetPin(jwtToken: String): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Sends the state for the biometric activation.
     *
     * @param jwtToken the current token of the user
     * @param countryCode the countryCode of the user
     * @param currentPin the current user pin; only required if [state] is [UserBiometricState.ENABLED]
     * @param state the new state
     */
    fun sendBiometricActivation(
        jwtToken: String,
        countryCode: String,
        state: UserBiometricState,
        currentPin: String?
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the user unit preferences.
     *
     * @param jwtToken the current token of the user
     * @param unitPreferences a collection of unit preferences to be saved
     */
    fun updateUnitPreferences(
        jwtToken: String,
        unitPreferences: UnitPreferences
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the user's adaption values.
     *
     * @param jwtToken the current token of the user
     * @param bodyHeight the body height parameters of the user
     */
    fun updateAdaptionValues(jwtToken: String, bodyHeight: UserBodyHeight): FutureTask<Unit, ResponseError<out RequestError>?>

    fun fetchProfileFields(countryCode: String, locale: String? = null): FutureTask<ProfileFieldsData, ResponseError<out RequestError>?>

    /**
     * Fetches and caches the Terms and Conditions for CIAM for the given locale and country.
     * The service will always try to fetch most recent ToU, the cached ToU are only returned
     * if the network request failed.
     */
    fun fetchCiamTermsAndConditions(
        countryCode: String,
        locale: String? = null
    ): FutureTask<UserAgreements<CiamUserAgreement>, ResponseError<out RequestError>?>

    /**
     * Fetches and caches the Terms and Conditions for SOE for the given locale and country.
     * The service will always try to fetch most recent ToU, the cached ToU are only returned
     * if the network request failed.
     */
    fun fetchSOETermsAndConditions(
        jwtToken: String,
        country: String,
        locale: String? = null
    ): FutureTask<UserAgreements<SoeUserAgreement>, ResponseError<out RequestError>?>

    /**
     * Fetches and caches the Terms and Conditions for the national consent
     * for the given locale and country.
     * The service will always try to fetch most recent ToU, the cached ToU are only returned
     * if the network request failed.
     */
    fun fetchNatconTermsAndConditions(
        countryCode: String,
        locale: String? = null
    ): FutureTask<UserAgreements<NatconUserAgreement>, ResponseError<out RequestError>?>

    /**
     * Fetches and caches the custom app Terms and Conditions for the given locale and country.
     * The service will always try to fetch most recent ToU, the cached ToU are only returned
     * if the network request failed.
     */
    fun fetchCustomTermsAndConditions(
        countryCode: String,
        jwtToken: String?,
        locale: String? = null
    ): FutureTask<UserAgreements<CustomUserAgreement>, ResponseError<out RequestError>?>

    /**
     * Fetches and caches the ldsso app Terms and Conditions for the given locale and country.
     * The service will always try to fetch most recent ToU, the cached ToU are only returned
     * if the network request failed.
     */
    fun fetchLdssoTermsAndConditions(
        countryCode: String,
        jwtToken: String?,
        ldssoAppId: String,
        ldssoVersionId: String,
        locale: String? = null
    ): FutureTask<UserAgreements<LdssoUserAgreement>, ResponseError<out RequestError>?>

    /**
     * Updates the acceptance state for the given Ciam ToU.
     */
    fun updateCiamAgreements(
        jwtToken: String,
        agreements: UserAgreementUpdates,
        locale: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the acceptance state for the given Natcon ToU.
     */
    fun updateNatconAgreements(
        jwtToken: String,
        agreements: UserAgreementUpdates,
        locale: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the acceptance state for the given Custom ToU.
     */
    fun updateCustomAgreements(
        jwtToken: String,
        agreements: UserAgreementUpdates,
        locale: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the acceptance state for the given LDSSO ToU.
     */
    fun updateLdssoAgreements(
        jwtToken: String,
        agreements: UserAgreementUpdates,
        ldssoAppId: String,
        ldssoVersionId: String,
        locale: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Updates the acceptance state for the given SOE ToU.
     */
    fun updateSOEAgreements(
        jwtToken: String,
        agreements: UserAgreementUpdates,
        locale: String? = null
    ): FutureTask<Unit, ResponseError<out RequestError>?>

    /**
     * Registers a scanReference for the current user.
     */
    fun verifyUser(
        jwtToken: String,
        scanReference: String
    ): FutureTask<Unit, ResponseError<out RequestError>?>
}