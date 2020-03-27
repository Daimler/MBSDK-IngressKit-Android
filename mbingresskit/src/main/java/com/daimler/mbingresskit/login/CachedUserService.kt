package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.CiamUserAgreement
import com.daimler.mbingresskit.common.Countries
import com.daimler.mbingresskit.common.CustomUserAgreement
import com.daimler.mbingresskit.common.LdssoUserAgreement
import com.daimler.mbingresskit.common.NatconUserAgreement
import com.daimler.mbingresskit.common.ProfileFieldsData
import com.daimler.mbingresskit.common.SoeUserAgreement
import com.daimler.mbingresskit.common.UnitPreferences
import com.daimler.mbingresskit.common.User
import com.daimler.mbingresskit.common.UserAgreementUpdates
import com.daimler.mbingresskit.common.UserAgreements
import com.daimler.mbingresskit.common.UserBodyHeight
import com.daimler.mbingresskit.persistence.CountryCache
import com.daimler.mbingresskit.persistence.ProfileFieldsCache
import com.daimler.mbingresskit.persistence.UserAgreementsCache
import com.daimler.mbingresskit.persistence.UserCache
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.TaskObject

/**
 * This class extends [UserService] to add possibility to load a cached user if required. All other
 * calls will directly be delegated to the passed [userServiceDelegate]
 */
internal class CachedUserService(
    private val userServiceDelegate: UserService,
    private val userCache: UserCache,
    private val agreementsCache: UserAgreementsCache,
    private val profileFieldsCache: ProfileFieldsCache,
    private val countryCache: CountryCache,
    private val headerService: HeaderService
) : UserService by userServiceDelegate {

    override fun loadUser(jwtToken: String): FutureTask<User, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<User, ResponseError<out RequestError>?>()
        userServiceDelegate.loadUser(jwtToken)
                .onComplete {
                    userCache.createOrUpdateUser(it)
                    deferredTask.complete(it)
                }.onFailure { error ->
                    userCache.loadUser()?.let {
                        deferredTask.complete(it)
                    } ?: deferredTask.fail(error)
                }
        return deferredTask.futureTask()
    }

    override fun updateUser(jwtToken: String, user: User): FutureTask<User, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<User, ResponseError<out RequestError>?>()
        userServiceDelegate.updateUser(jwtToken, user)
                .onComplete {
                    userCache.createOrUpdateUser(it)
                    deferredTask.complete(it)
                }.onFailure {
                    deferredTask.fail(it)
                }
        return deferredTask.futureTask()
    }

    override fun deleteUser(jwtToken: String, user: User): FutureTask<Unit, ResponseError<out RequestError>?> {
        return userServiceDelegate.deleteUser(jwtToken, user)
    }

    override fun updateProfilePicture(jwtToken: String, bitmapByteArray: ByteArray, mediaType: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateProfilePicture(jwtToken, bitmapByteArray, mediaType)
                .onComplete {
                    userCache.updateUserImage(bitmapByteArray)
                    deferredTask.complete(it)
                }.onFailure {
                    deferredTask.fail(it)
                }
        return deferredTask.futureTask()
    }

    override fun fetchProfilePictureBytes(jwtToken: String): FutureTask<ByteArray, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<ByteArray, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchProfilePictureBytes(jwtToken)
                .onComplete {
                    userCache.updateUserImage(it)
                    deferredTask.complete(it)
                }.onFailure { error ->
                    userCache.loadUserImage()?.let {
                        deferredTask.complete(it)
                    } ?: deferredTask.fail(error)
                }
        return deferredTask.futureTask()
    }

    override fun fetchCountries(): FutureTask<Countries, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Countries, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchCountries()
            .onComplete {
                countryCache.overwriteCache(it.countries, headerService.currentNetworkLocale())
                deferredTask.complete(it)
            }.onFailure { error ->
                countryCache.loadCountries(headerService.currentNetworkLocale())?.let {
                    deferredTask.complete(Countries(it))
                } ?: deferredTask.fail(error)
            }
        return deferredTask.futureTask()
    }

    override fun updateUnitPreferences(jwtToken: String, unitPreferences: UnitPreferences): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateUnitPreferences(jwtToken, unitPreferences)
                .onComplete { unit ->
                    userCache.updateUser { it.copy(unitPreferences = unitPreferences) }
                    deferredTask.complete(unit)
                }.onFailure {
                    deferredTask.fail(it)
                }
        return deferredTask.futureTask()
    }

    override fun updateAdaptionValues(jwtToken: String, bodyHeight: UserBodyHeight): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateAdaptionValues(jwtToken, bodyHeight)
                .onComplete { unit ->
                    userCache.updateUser { it.copy(bodyHeight = bodyHeight) }
                    deferredTask.complete(unit)
                }.onFailure {
                    deferredTask.fail(it)
                }
        return deferredTask.futureTask()
    }

    override fun fetchProfileFields(countryCode: String, locale: String?): FutureTask<ProfileFieldsData, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<ProfileFieldsData, ResponseError<out RequestError>?>()
        val localeToUse = locale ?: headerService.currentNetworkLocale()
        userServiceDelegate.fetchProfileFields(countryCode, localeToUse)
                .onComplete {
                    profileFieldsCache.createOrUpdateProfileFields(countryCode, localeToUse, it)
                    deferredTask.complete(it)
                }.onFailure { error ->
                    profileFieldsCache.loadProfileFields(countryCode, localeToUse)?.let {
                        deferredTask.complete(it)
                    } ?: deferredTask.fail(error)
                }
        return deferredTask.futureTask()
    }

    override fun fetchCiamTermsAndConditions(countryCode: String, locale: String?): FutureTask<UserAgreements<CiamUserAgreement>, ResponseError<out RequestError>?> {
        val task = TaskObject<UserAgreements<CiamUserAgreement>, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchCiamTermsAndConditions(countryCode, locale)
                .onComplete { agreements ->
                    val result = agreements.agreements.map { agreement ->
                        val updatedAgreement = agreementsCache.writeCiamAgreement(agreement)
                        updatedAgreement?.let { it } ?: agreement
                    }
                    task.complete(agreements.copy(agreements = result))
                }.onFailure {
                    val cachedAgreements =
                            agreementsCache.readCiamAgreements(locale ?: headerService.currentNetworkLocale(), countryCode)
                    if (cachedAgreements != null && cachedAgreements.agreements.isNotEmpty()) {
                        task.complete(cachedAgreements)
                    } else {
                        task.fail(it)
                    }
                }
        return task.futureTask()
    }

    override fun fetchSOETermsAndConditions(jwtToken: String, country: String, locale: String?): FutureTask<UserAgreements<SoeUserAgreement>, ResponseError<out RequestError>?> {
        val task = TaskObject<UserAgreements<SoeUserAgreement>, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchSOETermsAndConditions(jwtToken, country, locale)
                .onComplete { agreements ->
                    val result = agreements.agreements.map { agreement ->
                        val updatedAgreement = agreementsCache.writeSoeAgreement(agreement)
                        updatedAgreement?.let { it } ?: agreement
                    }
                    task.complete(agreements.copy(agreements = result))
                }.onFailure {
                    val cachedAgreements =
                            agreementsCache.readSoeAgreements(locale ?: headerService.currentNetworkLocale(), country)
                    if (cachedAgreements != null && cachedAgreements.agreements.isNotEmpty()) {
                        task.complete(cachedAgreements)
                    } else {
                        task.fail(it)
                    }
                }
        return task.futureTask()
    }

    override fun fetchNatconTermsAndConditions(countryCode: String, locale: String?): FutureTask<UserAgreements<NatconUserAgreement>, ResponseError<out RequestError>?> {
        val task = TaskObject<UserAgreements<NatconUserAgreement>, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchNatconTermsAndConditions(countryCode, locale)
                .onComplete { agreements ->
                    val result = agreements.agreements.map { agreement ->
                        val updatedAgreement = agreementsCache.writeNatconAgreement(agreement)
                        updatedAgreement?.let { it } ?: agreement
                    }
                    task.complete(agreements.copy(agreements = result))
                }.onFailure {
                    val cachedAgreements =
                            agreementsCache.readNatconAgreements(locale ?: headerService.currentNetworkLocale(), countryCode)
                    if (cachedAgreements != null && cachedAgreements.agreements.isNotEmpty()) {
                        task.complete(cachedAgreements)
                    } else {
                        task.fail(it)
                    }
                }
        return task.futureTask()
    }

    override fun fetchCustomTermsAndConditions(countryCode: String, jwtToken: String?, locale: String?): FutureTask<UserAgreements<CustomUserAgreement>, ResponseError<out RequestError>?> {
        val task = TaskObject<UserAgreements<CustomUserAgreement>, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchCustomTermsAndConditions(countryCode, jwtToken, locale)
            .onComplete { agreements ->
                val result = agreements.agreements.map { agreement ->
                    val updatedAgreement = agreementsCache.writeCustomAgreement(agreement)
                    updatedAgreement?.let { it } ?: agreement
                }
                task.complete(agreements.copy(agreements = result))
            }.onFailure {
                val cachedAgreements =
                    agreementsCache.readCustomAgreements(locale ?: headerService.currentNetworkLocale(), countryCode)
                if (cachedAgreements != null && cachedAgreements.agreements.isNotEmpty()) {
                    task.complete(cachedAgreements)
                } else {
                    task.fail(it)
                }
            }
        return task.futureTask()
    }

    override fun fetchLdssoTermsAndConditions(countryCode: String, jwtToken: String?, ldssoAppId: String, ldssoVersionId: String, locale: String?): FutureTask<UserAgreements<LdssoUserAgreement>, ResponseError<out RequestError>?> {
        val task = TaskObject<UserAgreements<LdssoUserAgreement>, ResponseError<out RequestError>?>()
        userServiceDelegate.fetchLdssoTermsAndConditions(countryCode, jwtToken, ldssoAppId, ldssoVersionId, locale)
                .onComplete { agreements ->
                    val result = agreements.agreements.map { agreement ->
                        val updatedAgreement = agreementsCache.writeLdssoAgreement(agreement)
                        updatedAgreement?.let { it } ?: agreement
                    }
                    task.complete(agreements.copy(agreements = result))
                }.onFailure {
                    val cachedAgreements =
                            agreementsCache.readLdssoAgreements(locale ?: headerService.currentNetworkLocale(), countryCode)
                    if (cachedAgreements != null && cachedAgreements.agreements.isNotEmpty()) {
                        task.complete(cachedAgreements)
                    } else {
                        task.fail(it)
                    }
                }
        return task.futureTask()
    }

    override fun updateCiamAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateCiamAgreements(jwtToken, agreements, locale)
            .onComplete {
                agreements.updates.forEach { update ->
                    agreementsCache.updateCiamAcceptance(
                        update.userAgreementId, update.accepted
                    )
                }
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(it)
            }
        return deferredTask.futureTask()
    }

    override fun updateCustomAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateCustomAgreements(jwtToken, agreements, locale)
            .onComplete {
                agreements.updates.forEach { update ->
                    agreementsCache.updateCustomAcceptance(
                        update.userAgreementId, update.accepted
                    )
                }
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(it)
            }
        return deferredTask.futureTask()
    }

    override fun updateLdssoAgreements(jwtToken: String, agreements: UserAgreementUpdates, ldssoAppId: String, ldssoVersionId: String, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateLdssoAgreements(jwtToken, agreements, ldssoAppId, ldssoVersionId, locale)
            .onComplete {
                agreements.updates.forEach { update ->
                    agreementsCache.updateLdssoAcceptance(
                        update.userAgreementId, update.accepted
                    )
                }
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(it)
            }
        return deferredTask.futureTask()
    }

    override fun updateNatconAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateNatconAgreements(jwtToken, agreements, locale)
            .onComplete {
                agreements.updates.forEach { update ->
                    agreementsCache.updateNatconAcceptance(
                        update.userAgreementId, update.accepted
                    )
                }
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(it)
            }
        return deferredTask.futureTask()
    }

    override fun updateSOEAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        userServiceDelegate.updateSOEAgreements(jwtToken, agreements, locale)
            .onComplete {
                agreements.updates.forEach { update ->
                    agreementsCache.updateSoeAcceptance(
                        update.userAgreementId, update.accepted
                    )
                }
                deferredTask.complete(it)
            }.onFailure {
                deferredTask.fail(it)
            }
        return deferredTask.futureTask()
    }
}