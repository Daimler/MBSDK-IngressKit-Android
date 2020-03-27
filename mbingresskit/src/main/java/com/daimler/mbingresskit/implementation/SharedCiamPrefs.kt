package com.daimler.mbingresskit.implementation

import android.content.Context
import android.content.SharedPreferences
import com.daimler.mbingresskit.ingress.AuthstateRepository
import com.daimler.mbingresskit.ingress.CiamEndpoint
import com.daimler.mbingresskit.ingress.CiamEndpointRepository
import com.daimler.mbingresskit.ingress.CiamEnvironment
import com.daimler.mbingresskit.common.AuthPrompt
import com.daimler.mbingresskit.common.AuthScope
import com.daimler.mbingresskit.common.AuthenticationState
import com.daimler.mbingresskit.common.Prompt
import com.daimler.mbingresskit.login.LoginService
import com.daimler.mbingresskit.login.LoginServiceNameRepository
import com.google.gson.Gson
import org.json.JSONException

class SharedCiamPrefs(context: Context) : AuthstateRepository, LoginServiceNameRepository, CiamEndpointRepository {

    companion object {
        val KEY_LOGIN_SERVICE = LoginService::class.java.simpleName
        val KEY_AUTH_STATE = AuthenticationState::class.java.simpleName
        val KEY_CIAM_ENDPOINT = CiamEndpoint::class.java.simpleName
    }

    private val SETTINGS_NAME = "${context.packageName}_ciam_prefs"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    private val gson: Gson = Gson()

    override fun saveAuthState(authState: AuthenticationState) {
        clearAuthState()
        sharedPreferences.edit {
            putString(KEY_AUTH_STATE, gson.toJson(authState))
        }
    }

    override fun clearAuthState() {
        sharedPreferences.edit {
            remove(KEY_AUTH_STATE)
        }
    }

    override fun readAuthState(): AuthenticationState {
        val jsonAuthState = sharedPreferences.getString(KEY_AUTH_STATE, "")
        return if (jsonAuthState.isNotEmpty()) try {
            gson.fromJson(jsonAuthState, AuthenticationState::class.java)
        } catch (jsa: JSONException) {
            AuthenticationState()
        } else AuthenticationState()
    }

    override fun saveLoginServiceName(loginService: LoginService) {
        sharedPreferences.edit {
            putString(KEY_LOGIN_SERVICE, loginService::class.java.simpleName)
        }
    }

    override fun loadLoginServiceName(): String {
        return sharedPreferences.getString(KEY_LOGIN_SERVICE, "")
    }

    override fun clearLoginServiceName() {
        sharedPreferences.edit {
            remove(KEY_LOGIN_SERVICE)
        }
    }

    override fun saveEnvironment(endpoint: CiamEndpoint) {
        sharedPreferences.edit {
            putString(KEY_CIAM_ENDPOINT, gson.toJson(endpoint))
        }
    }

    override fun loadEndpoint(): CiamEndpoint {
        val ciamEndpointString = sharedPreferences.getString(KEY_CIAM_ENDPOINT, "")
        return if (ciamEndpointString.isNotEmpty()) try {
            gson.fromJson(ciamEndpointString, CiamEndpoint::class.java)
        } catch (jsa: JSONException) {
            CiamEndpoint(CiamEnvironment.INT, AuthScope(), authPrompt = AuthPrompt(Prompt.LOGIN))
        } else CiamEndpoint(CiamEnvironment.INT, AuthScope(), authPrompt = AuthPrompt(Prompt.LOGIN))
    }
}