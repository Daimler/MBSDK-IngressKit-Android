package com.daimler.mbingresskit.implementation

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import com.daimler.mbcommonkit.extensions.getEncryptedSharedPreferences
import com.daimler.mbingresskit.ingress.AuthstateRepository
import com.daimler.mbingresskit.ingress.CiamEndpoint
import com.daimler.mbingresskit.ingress.CiamEndpointRepository
import com.daimler.mbingresskit.ingress.CiamEnvironment
import com.daimler.mbingresskit.common.AuthPrompt
import com.daimler.mbingresskit.common.AuthScope
import com.daimler.mbingresskit.common.AuthenticationState
import com.daimler.mbingresskit.common.Prompt
import com.daimler.mbingresskit.login.AuthenticationStateTokenState
import com.daimler.mbingresskit.login.LoginService
import com.daimler.mbingresskit.login.LoginServiceNameRepository
import com.daimler.mbingresskit.login.TokenState
import com.google.gson.Gson
import org.json.JSONException

class SsoAccountPrefs(
    private val context: Context,
    private val sharedUserId: String,
    private val keyStoreAlias: String
) : AuthstateRepository, LoginServiceNameRepository, CiamEndpointRepository {

    companion object {
        private const val KEY_AUTH_STATE = "AuthenticationState"
        private const val KEY_LOGIN_SERVICE = "LoginService"
        private const val KEY_CIAM_ENDPOINT = "CiamEndpoint"

        private const val SETTINGS_NAME = "account_prefs"
    }

    private val gson: Gson = Gson()

    init {
        checkSharedUserIdconfigured(sharedUserId)
        checkInitialization()
    }

    private fun checkSharedUserIdconfigured(sharedUserId: String) {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        if (packageInfo.sharedUserId != sharedUserId) {
            throw SharedUserIdNotSetException(sharedUserId)
        }
    }

    override fun saveAuthState(authState: AuthenticationState) {
        clearAuthState()
        packageInfosWithSharedUserId(sharedUserId).forEach {
            getPreferences(context.createPackageContext(it.packageName, 0)).edit(true) {
                putString(KEY_AUTH_STATE, gson.toJson(authState))
            }
        }
    }

    override fun clearAuthState() {
        packageInfosWithSharedUserId(sharedUserId).forEach {
            getPreferences(context.createPackageContext(it.packageName, 0)).edit(true) {
                remove(KEY_AUTH_STATE)
            }
        }
    }

    override fun readAuthState(): AuthenticationState {
        val jsonAuthState = getPreferences(context).getString(KEY_AUTH_STATE, "")
        return authStateFromJson(jsonAuthState)
    }

    override fun saveLoginServiceName(loginService: LoginService) {
        packageInfosWithSharedUserId(sharedUserId).forEach {
            getPreferences(context.createPackageContext(it.packageName, 0)).edit(true) {
                putString(KEY_LOGIN_SERVICE, loginService::class.java.simpleName)
            }
        }
    }

    override fun loadLoginServiceName(): String {
        return getPreferences(context).getString(KEY_LOGIN_SERVICE, "") ?: ""
    }

    override fun clearLoginServiceName() {
        packageInfosWithSharedUserId(sharedUserId).forEach {
            getPreferences(context.createPackageContext(it.packageName, 0)).edit(true) {
                remove(KEY_LOGIN_SERVICE)
            }
        }
    }

    override fun saveEnvironment(endpoint: CiamEndpoint) {
        packageInfosWithSharedUserId(sharedUserId).forEach {
            getPreferences(context.createPackageContext(it.packageName, 0)).edit(true) {
                putString(KEY_CIAM_ENDPOINT, gson.toJson(endpoint))
            }
        }
    }

    override fun loadEndpoint(): CiamEndpoint {
        val ciamEndpointString = getPreferences(context).getString(KEY_CIAM_ENDPOINT, "")
        return if (ciamEndpointString.isNotEmpty()) try {
            gson.fromJson(ciamEndpointString, CiamEndpoint::class.java)
        } catch (jsa: JSONException) {
            CiamEndpoint(CiamEnvironment.INT, AuthScope(), authPrompt = AuthPrompt(Prompt.LOGIN))
        } else CiamEndpoint(CiamEnvironment.INT, AuthScope(), authPrompt = AuthPrompt(Prompt.LOGIN))
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getEncryptedSharedPreferences(keyStoreAlias, SETTINGS_NAME, Context.MODE_MULTI_PROCESS)
    }

    private fun packageInfosWithSharedUserId(sharedUserId: String): List<PackageInfo> {
        val rsPackages = context.packageManager.getInstalledPackages(0)
        return rsPackages.filter {
            it.packageName.contains(sharedUserId).and(it.sharedUserId == sharedUserId)
        }
    }

    private fun checkInitialization() {
        val currentAuthState = getPreferences(context).getString(KEY_AUTH_STATE, "")
        if (currentAuthState.isNotEmpty()) return

        packageInfosWithSharedUserId(sharedUserId).forEach { pInfo ->
            val preferences =
                    getPreferences(context.createPackageContext(pInfo.packageName, 0))
            val rsAuthStateJson = preferences.getString(KEY_AUTH_STATE, "")
            val rsAuthState = authStateFromJson(rsAuthStateJson)
            val tokenState = AuthenticationStateTokenState(rsAuthState).getTokenState()
            if (tokenState !is TokenState.LOGGEDOUT) {
                copyPreferencesFrom(preferences)
                return
            }
        }
    }

    private fun copyPreferencesFrom(preferences: SharedPreferences) {
        val localPreferences = getPreferences(context)
        copyPreferencesValues(preferences, localPreferences,
                listOf(KEY_AUTH_STATE, KEY_LOGIN_SERVICE, KEY_CIAM_ENDPOINT), "")
    }

    private fun copyPreferencesValues(
        from: SharedPreferences,
        to: SharedPreferences,
        keys: List<String>,
        default: String
    ) {
        to.edit(true) {
            keys.forEach {
                val value = from.getString(it, default)
                putString(it, value)
            }
        }
    }

    private fun authStateFromJson(json: String) =
            if (json.isNotEmpty()) try {
                gson.fromJson(json, AuthenticationState::class.java)
            } catch (jsa: JSONException) {
                AuthenticationState()
            } else AuthenticationState()

    inner class SharedUserIdNotSetException(
        expectedId: String
    ) : IllegalArgumentException("To share account between apps, the sharedUserId $expectedId must be configured in applications manifest too")
}