package com.daimler.mbingresskit.implementation.network

import android.os.AsyncTask
import com.daimler.mbingresskit.common.AgreementAcceptanceState
import com.daimler.mbingresskit.common.UserAgreement
import com.daimler.mbingresskit.common.UserAgreements
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAcceptedByUser
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.RawAgreementsResponse

/**
 * AsyncTask responsible to load contents for user agreements.
 */
internal abstract class BaseAgreementTask<R : Any, T : UserAgreement>(
    protected val jwtToken: String,
    protected val locale: String,
    protected val country: String,
    protected val api: UserApi,
    private var callback: Callback<T>?
) : AsyncTask<RawAgreementsResponse, Void, BaseAgreementTask.Result<T>>() {

    override fun doInBackground(vararg params: RawAgreementsResponse): Result<T> {
        return try {
            if (params.isNotEmpty()) {
                loadContent(params.first())
            } else {
                Result(null, IllegalArgumentException("No input given."))
            }
        } catch (e: Exception) {
            Result(null, e)
        }
    }

    override fun onPostExecute(result: Result<T>) {
        super.onPostExecute(result)
        result.result?.let { callback?.onComplete(it) } ?: callback?.onFailure(result.error)
        callback = null
    }

    protected abstract fun getAgreements(agreements: ApiAgreementsResponse): List<R>

    protected abstract fun fetchAgreementContent(agreement: R): T

    protected fun mapApiAcceptanceStateToAcceptanceState(apiState: ApiAcceptedByUser?): AgreementAcceptanceState =
            when (apiState) {
                ApiAcceptedByUser.ACCEPTED -> AgreementAcceptanceState.ACCEPTED
                ApiAcceptedByUser.REJECTED -> AgreementAcceptanceState.REJECTED
                ApiAcceptedByUser.UNSEEN_VIA_RISINGSTARS -> AgreementAcceptanceState.UNSEEN_VIA_RISING_STARS
                null -> AgreementAcceptanceState.UNKNOWN
            }

    private fun loadContent(response: RawAgreementsResponse): Result<T> {
        val agreements = getAgreements(response.response)
        return if (agreements.isNotEmpty()) {
            val result = mutableListOf<T>()
            agreements.forEach {
                val agreement = fetchAgreementContent(it)
                result.add(agreement)
            }
            Result(UserAgreements(locale, country, result), null)
        } else {
            Result(null, IllegalArgumentException("List of agreements was empty."))
        }
    }

    interface Callback<T : UserAgreement> {
        fun onComplete(agreements: UserAgreements<T>)
        fun onFailure(error: Throwable?)
    }

    internal data class Result<T : UserAgreement>(val result: UserAgreements<T>?, val error: Throwable?)
}