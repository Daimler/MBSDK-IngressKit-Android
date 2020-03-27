package com.daimler.mbingresskit.example

import android.app.Application
import android.os.Build
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbloggerkit.PrinterConfig
import com.daimler.mbloggerkit.adapter.AndroidLogAdapter
import com.daimler.mbloggerkit.adapter.PersistingLogAdapter
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbingresskit.IngressServiceConfig
import com.daimler.mbingresskit.login.SessionExpiredHandler
import com.daimler.mbnetworkkit.MBNetworkKit
import com.daimler.mbnetworkkit.NetworkServiceConfig
import java.util.*

class ExampleApplication : Application() {

    @Suppress("ConstantConditionIf")
    companion object {
        private const val USE_PROD = true

        private const val JWT_URL_INT = "https://auth-int.risingstars-int.daimler.com"
        private const val JWT_URL_PROD = "https://auth-prod.risingstars.daimler.com"
        val JWT_URL = if (USE_PROD) JWT_URL_PROD else JWT_URL_INT

        private const val AUTH_URL_INT = "https://keycloak.risingstars-int.daimler.com"
        private const val AUTH_URL_PROD = "https://keycloak.risingstars.daimler.com"
        val AUTH_URL = if (USE_PROD) AUTH_URL_PROD else AUTH_URL_INT

        private const val USER_URL_INT = "https://bff-int.risingstars-int.daimler.com"
        private const val USER_URL_PROD = "https://bff-prod.risingstars.daimler.com"
        val USER_URL = if (USE_PROD) USER_URL_PROD else USER_URL_INT

        private const val INGRESS_STAGE_INT = "int"
        private const val INGRESS_STAGE_PROD = "prod"
        val INGRESS_STAGE = if (USE_PROD) INGRESS_STAGE_PROD else INGRESS_STAGE_INT
    }

    override fun onCreate() {
        super.onCreate()
        MBNetworkKit.init(
            NetworkServiceConfig.Builder("reference", "1.0", "1.0.63")
                .apply {
                    useOSVersion(Build.VERSION.RELEASE)
                    useLocale("de-DE")
                }.build()
        )
        val serviceConfigBuilder = IngressServiceConfig.Builder(this,
            AUTH_URL, USER_URL, INGRESS_STAGE, "com.daimler.ingress.test.sample.alias", MBNetworkKit.headerService(), "app")
            .useSessionExpiredHandler(object : SessionExpiredHandler {
                override fun onSessionExpired(statusCode: Int, errorBody: String?) {
                    MBLoggerKit.e("session expired: $statusCode, $errorBody")
                }
            })
            .useDeviceId(UUID.randomUUID().toString())

        MBLoggerKit.usePrinterConfig(PrinterConfig.Builder()
            .addAdapter(AndroidLogAdapter.Builder()
                .setLoggingEnabled(BuildConfig.DEBUG)
                .build())
            .addAdapter(PersistingLogAdapter.Builder(this)
                .setLoggingEnabled(BuildConfig.DEBUG)
                .build())
            .build())

        MBIngressKit.init(serviceConfigBuilder.build())
    }
}
