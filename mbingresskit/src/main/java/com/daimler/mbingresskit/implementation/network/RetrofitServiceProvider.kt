package com.daimler.mbingresskit.implementation.network

import android.content.Context
import android.net.ConnectivityManager
import com.daimler.mbloggerkit.Priority
import com.daimler.mbnetworkkit.networking.createHttpLoggingInterceptor
import com.daimler.mbingresskit.BuildConfig
import com.daimler.mbnetworkkit.certificatepinning.CertificateConfiguration
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinnerProvider
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinningErrorProcessor
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinningInterceptor
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.networking.ConnectivityInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceProvider {
    companion object {

        fun createKeycloakApi(
            context: Context,
            baseUrl: String,
            enableLogging: Boolean,
            certificatePinningErrorProcessor: CertificatePinningErrorProcessor?,
            certificatePinnerProvider: CertificatePinnerProvider,
            pinningConfigurations: List<CertificateConfiguration>
        ): KeycloakApi {
            return Retrofit.Builder()
                    .client(OkHttpClient.Builder().apply {
                            addInterceptor(ConnectivityInterceptor(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager))
                            if (enableLogging) addInterceptor(loggingInterceptor())
                            if (pinningConfigurations.isNotEmpty()) {
                                certificatePinner(certificatePinnerProvider.createCertificatePinner(pinningConfigurations))
                                certificatePinningErrorProcessor?.let { addInterceptor(CertificatePinningInterceptor(it)) }
                            }
                        }
                        .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
                    .create(KeycloakApi::class.java)
        }

        fun createUserApi(
            context: Context,
            baseUrl: String,
            enableLogging: Boolean,
            headerService: HeaderService,
            certificatePinningErrorProcessor: CertificatePinningErrorProcessor?,
            certificatePinnerProvider: CertificatePinnerProvider,
            pinningConfigurations: List<CertificateConfiguration>
        ): UserApi {
            return Retrofit.Builder()
                    .client(OkHttpClient.Builder().apply {
                            addInterceptor(ConnectivityInterceptor(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager))
                            addInterceptor(headerService.createRisHeaderInterceptor())
                            if (enableLogging) addInterceptor(loggingInterceptor())
                            if (pinningConfigurations.isNotEmpty()) {
                                certificatePinner(certificatePinnerProvider.createCertificatePinner(pinningConfigurations))
                                certificatePinningErrorProcessor?.let { addInterceptor(CertificatePinningInterceptor(it)) }
                            }
                        }
                        .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
                    .create(UserApi::class.java)
        }

        private fun loggingInterceptor(): HttpLoggingInterceptor {
            return createHttpLoggingInterceptor(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.HEADERS,
                    Priority.INFO
            )
        }
    }
}