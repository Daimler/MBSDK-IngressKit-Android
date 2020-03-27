package com.daimler.mbingresskit.persistence

import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.persistence.model.*
import com.daimler.mbloggerkit.MBLoggerKit
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.delete
import io.realm.kotlin.where

internal class RealmUserCache(private val realm: Realm) : UserCache {

    override fun createOrUpdateUser(user: User) {
        val id = user.ciamId
        var realmUser = loadUserById(id)
        realm.executeTransaction {
            if (realmUser == null) {
                MBLoggerKit.d("Creating user in database cache.")
                realmUser = it.createObject(id)
            } else {
                MBLoggerKit.d("Updating user in database cache.")
            }
            realmUser?.apply {
                userId = user.userId
                firstName = user.firstName
                lastName = user.lastName
                birthday = user.birthday
                email = user.email
                mobilePhone = user.mobilePhone
                landlinePhone = user.landlinePhone
                countryCode = user.countryCode
                languageCode = user.languageCode
                createdAt = user.createdAt
                updatedAt = user.updatedAt
                pinStatus = user.pinStatus.ordinal
                address = createOrUpdateUserAddress(user.address, address)
                communicationPreference = createOrUpdateCommunicationPreferences(user.communicationPreference, communicationPreference)
                unitPreferences = createOrUpdateUnitPreferences(user.unitPreferences, unitPreferences)
                accountIdentifier = user.accountIdentifier.ordinal
                title = user.title
                salutationCode = user.salutationCode
                taxNumber = user.taxNumber
                bodyHeight = createOrUpdateUserBodyHeight(user.bodyHeight, bodyHeight)
                accountVerified = user.accountVerified

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    override fun updateUser(action: (User) -> User) {
        val result = loadUser()?.let(action)
        result?.let { createOrUpdateUser(it) }
    }

    override fun loadUser(): User? {
        return loadRealmUser()?.let { mapRealmUserToUser(it) }
    }

    override fun updateUserImage(imageBytes: ByteArray?) {
        loadRealmUser()?.let { realmUser ->
            realm.executeTransaction {
                realmUser.imageBytes = imageBytes

                it.copyToRealmOrUpdate(realmUser)
            }
        }
    }

    override fun loadUserImage(): ByteArray? {
        return loadRealmUser()?.imageBytes
    }

    override fun clear() {
        deleteAllUsers()
    }

    private fun loadUserById(id: String): RealmUser? =
        realm.where<RealmUser>()
            .equalTo(RealmUser.FIELD_ID, id)
            .findFirst()

    private fun loadRealmUser(): RealmUser? = realm.where<RealmUser>().findFirst()

    private fun deleteAllUsers() {
        realm.executeTransaction {
            it.delete<RealmCommunicationPreferences>()
            it.delete<RealmUserAddress>()
            it.delete<RealmUserBodyHeight>()
            it.delete<RealmUserUnitPreferences>()
            it.delete<RealmUser>()
        }
    }

    private fun createOrUpdateUserAddress(
        userAddress: Address?,
        realmUserAddress: RealmUserAddress?
    ): RealmUserAddress? {
        return userAddress?.let {
            val address = realmUserAddress ?: realm.createObject()
            address.apply {
                street = it.street
                houseNumber = it.houseNumber
                zipCode = it.zipCode
                city = it.city
                countryCode = it.countryCode
                state = it.state
                province = it.province
                streetType = it.streetType
                houseName = it.houseName
                floorNumber = it.floorNumber
                doorNumber = it.doorNumber
                addressLine1 = it.addressLine1
                addressLine2 = it.addressLine2
                addressLine3 = it.addressLine3
                postOfficeBox = it.postOfficeBox
            }
        } ?: realmUserAddress
    }

    private fun createOrUpdateCommunicationPreferences(
        userCommunicationPreference: CommunicationPreference,
        realmCommunicationPreferences: RealmCommunicationPreferences?
    ): RealmCommunicationPreferences {
        val communicationPreferences =
            realmCommunicationPreferences ?: realm.createObject()
        return communicationPreferences.apply {
            contactByPhone = userCommunicationPreference.contactByPhone
            contactByLetter = userCommunicationPreference.contactByLetter
            contactByMail = userCommunicationPreference.contactByMail
            contactBySms = userCommunicationPreference.contactBySms
        }
    }

    private fun createOrUpdateUnitPreferences(
        userUnitPreferences: UnitPreferences,
        realmUnitPreferences: RealmUserUnitPreferences?
    ): RealmUserUnitPreferences {
        val unitPreferences = realmUnitPreferences ?: realm.createObject()
        return unitPreferences.apply {
            clockHours = userUnitPreferences.clockHours.ordinal
            speedDistance = userUnitPreferences.speedDistance.ordinal
            consumptionCo = userUnitPreferences.consumptionCo.ordinal
            consumptionEv = userUnitPreferences.consumptionEv.ordinal
            consumptionGas = userUnitPreferences.consumptionGas.ordinal
            tirePressure = userUnitPreferences.tirePressure.ordinal
            temperature = userUnitPreferences.temperature.ordinal
        }
    }

    private fun createOrUpdateUserBodyHeight(
        userBodyHeight: UserBodyHeight?,
        realmUserBodyHeight: RealmUserBodyHeight?
    ): RealmUserBodyHeight? {
        return userBodyHeight?.let {
            val bodyHeight = realmUserBodyHeight ?: realm.createObject()
            bodyHeight.apply {
                this.bodyHeight = it.bodyHeight
                preAdjustment = it.preAdjustment
            }
        } ?: realmUserBodyHeight
    }

    private fun mapRealmUserToUser(realmUser: RealmUser) =
        User(
            ciamId = realmUser.ciamId,
            userId = realmUser.userId.orEmpty(),
            firstName = realmUser.firstName.orEmpty(),
            lastName = realmUser.lastName.orEmpty(),
            birthday = realmUser.birthday.orEmpty(),
            email = realmUser.email.orEmpty(),
            mobilePhone = realmUser.mobilePhone.orEmpty(),
            landlinePhone = realmUser.landlinePhone.orEmpty(),
            countryCode = realmUser.countryCode.orEmpty(),
            languageCode = realmUser.languageCode.orEmpty(),
            createdAt = realmUser.createdAt.orEmpty(),
            updatedAt = realmUser.updatedAt.orEmpty(),
            pinStatus = User.pinStatusFromInt(realmUser.pinStatus),
            address = mapRealmAddressToAddress(realmUser.address),
            communicationPreference = mapRealmCommunicationPreferencesToCommunicationPreferences(realmUser.communicationPreference),
            unitPreferences = mapRealmUnitPreferencesToUnitPreferences(realmUser.unitPreferences),
            accountIdentifier = User.accountIdentifierFromInt(realmUser.accountIdentifier),
            title = realmUser.title.orEmpty(),
            salutationCode = realmUser.salutationCode.orEmpty(),
            taxNumber = realmUser.taxNumber.orEmpty(),
            bodyHeight = mapRealmBodyHeightToBodyHeight(realmUser.bodyHeight),
            accountVerified = realmUser.accountVerified == true
        )

    private fun mapRealmAddressToAddress(realmUserAddress: RealmUserAddress?) =
        realmUserAddress?.let {
            Address(
                street = it.street,
                houseNumber = it.houseNumber,
                zipCode = it.zipCode,
                city = it.city,
                countryCode = it.countryCode,
                state = it.state,
                province = it.province,
                streetType = it.streetType,
                houseName = it.houseName,
                floorNumber = it.floorNumber,
                doorNumber = it.doorNumber,
                addressLine1 = it.addressLine1,
                addressLine2 = it.addressLine2,
                addressLine3 = it.addressLine3,
                postOfficeBox = it.postOfficeBox
            )
        }

    private fun mapRealmCommunicationPreferencesToCommunicationPreferences(
        realmCommunicationPreferences: RealmCommunicationPreferences?
    ) = realmCommunicationPreferences?.let {
        CommunicationPreference(
            contactByPhone = it.contactByPhone == true,
            contactByLetter = it.contactByLetter == true,
            contactByMail = it.contactByMail == true,
            contactBySms = it.contactBySms == true
        )
    } ?: CommunicationPreference.initialState()

    private fun mapRealmUnitPreferencesToUnitPreferences(
        realmUnitPreferences: RealmUserUnitPreferences?
    ) = realmUnitPreferences?.let {
        UnitPreferences(
            clockHours = UnitPreferences.clockHoursUnitFromInt(it.clockHours),
            speedDistance = UnitPreferences.speedDistanceUnitFromInt(it.speedDistance),
            consumptionCo = UnitPreferences.consumptionCoUnitFromInt(it.consumptionCo),
            consumptionEv = UnitPreferences.consumptionEvUnitFromInt(it.consumptionEv),
            consumptionGas = UnitPreferences.consumptionGasUnitFromInt(it.consumptionGas),
            tirePressure = UnitPreferences.tirePressureUnitFromInt(it.tirePressure),
            temperature = UnitPreferences.temperatureUnitFromInt(it.temperature)
        )
    } ?: UnitPreferences.defaultUnitPreferences()

    private fun mapRealmBodyHeightToBodyHeight(
        realmUserBodyHeight: RealmUserBodyHeight?
    ) = realmUserBodyHeight?.let {
        UserBodyHeight(
            bodyHeight = it.bodyHeight ?: 0,
            preAdjustment = it.preAdjustment == true
        )
    }
}