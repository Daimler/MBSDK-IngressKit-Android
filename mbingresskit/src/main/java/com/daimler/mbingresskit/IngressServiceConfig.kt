package com.daimler.mbingresskit

import android.content.Context
import com.daimler.mbingresskit.implementation.filestorage.JsonFileWriter
import com.daimler.mbingresskit.implementation.filestorage.ProfileFieldsFileStorage
import com.daimler.mbingresskit.implementation.filestorage.UserAgreementsFileStorageImpl
import com.daimler.mbingresskit.login.SessionExpiredHandler
import com.daimler.mbingresskit.persistence.*
import com.daimler.mbnetworkkit.certificatepinning.CertificateConfiguration
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinningErrorProcessor
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbrealmkit.MBRealmKit
import com.daimler.mbrealmkit.RealmServiceConfig
import java.util.*

class IngressServiceConfig private constructor(
    val context: Context,
    val sessionId: String,
    val authUrl: String,
    val userUrl: String,
    val sharedUserId: String,
    val ingressStage: String,
    val clientId: String,
    val sessionExpiredHandler: SessionExpiredHandler?,
    val keyStoreAlias: String,
    val deviceUuid: String,
    val userAgreementsCache: UserAgreementsCache,
    val userCache: UserCache,
    val profileFieldsCache: ProfileFieldsCache,
    val countryCache: CountryCache,
    val headerService: HeaderService,
    val pinningErrorProcessor: CertificatePinningErrorProcessor?,
    val pinningConfigurations: List<CertificateConfiguration>
) {

    /**
     * @param clientId The ClientId, is used to send login calls. If SSO is enabled, the passed [clientId]
     *          must be the same for all apps that will be published in the same SSO-Context.
     */
    class Builder(
        private val context: Context,
        private val authUrl: String,
        private val userUrl: String,
        private val ingressStage: String,
        private val keyStoreAlias: String,
        private val headerService: HeaderService,
        private val clientId: String
    ) {
        private var sharedUserId = ""
        private var sessionId: UUID? = null
        private var deviceUuid: String = ""
        private var sessionExpiredHandler: SessionExpiredHandler? = null
        private var pinningConfigurations: List<CertificateConfiguration> = emptyList()
        private var pinningErrorProcessor: CertificatePinningErrorProcessor? = null

        fun useAppSessionId(appSessionId: UUID): Builder {
            this.sessionId = appSessionId
            return this
        }

        fun useDeviceId(deviceId: String): Builder {
            this.deviceUuid = deviceId
            return this
        }

        fun enableSso(sharedUserId: String): Builder {
            this.sharedUserId = sharedUserId
            return this
        }

        fun useSessionExpiredHandler(sessionExpiredHandler: SessionExpiredHandler?): Builder {
            this.sessionExpiredHandler = sessionExpiredHandler
            return this
        }

        fun useCertificatePinning(pinningConfigurations: List<CertificateConfiguration>, errorProcessor: CertificatePinningErrorProcessor? = null): Builder {
            this.pinningConfigurations = pinningConfigurations
            this.pinningErrorProcessor = errorProcessor
            return this
        }

        fun build(): IngressServiceConfig {
            checkClientId()
            setupRealm()
            val userAgreementsCache: UserAgreementsCache = RealmUserAgreementsCache(
                MBRealmKit.realm(RealmUtil.ID_ENCR_INGRESS_REALM),
                UserAgreementsFileStorageImpl(context)
            )
            val userCache: UserCache = RealmUserCache(
                MBRealmKit.realm(RealmUtil.ID_ENCR_INGRESS_REALM)
            )
            val profileFieldsCache: ProfileFieldsCache = FileProfileFieldsCache(
                ProfileFieldsFileStorage(context, JsonFileWriter())
            )
            val countryCache: CountryCache = RealmCountryCache(
                MBRealmKit.realm(RealmUtil.ID_ENCR_INGRESS_REALM)
            )
            return IngressServiceConfig(context,
                sessionId?.toString() ?: UUID.randomUUID().toString(),
                authUrl,
                userUrl,
                sharedUserId,
                ingressStage,
                clientId,
                sessionExpiredHandler,
                keyStoreAlias,
                deviceUuid,
                userAgreementsCache,
                userCache,
                profileFieldsCache,
                countryCache,
                headerService,
                pinningErrorProcessor,
                pinningConfigurations
            )
        }

        private fun checkClientId() {
            if (invalidClientId()) {
                throw InvalidClientIdError()
            }
        }

        private fun invalidClientId(): Boolean {
            return clientId.isBlank() || clientId.isEmpty()
        }

        private fun setupRealm() {
            MBRealmKit.createRealmInstance(RealmUtil.ID_ENCR_INGRESS_REALM,
                RealmServiceConfig.Builder(context, RealmUtil.REALM_ENCR_SCHEMA_VERSION, MBIngressRealmModule())
                    .useDbName(RealmUtil.ENCR_INGRESS_FILE_NAME)
                    .encrypt()
                    .build())
        }

        class InvalidClientIdError : IllegalArgumentException("Empty ClientId was passed. To support login, a valid ClientId must be used when initializing MBIngressKit!")
    }
}