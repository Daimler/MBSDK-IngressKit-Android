package com.daimler.mbingresskit.example

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.auth0.android.jwt.JWT
import com.daimler.mbingresskit.MBIngressKit
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.login.LoginFailure
import com.daimler.mbingresskit.login.TokenState
import com.daimler.mbingresskit.util.IngressFileProvider
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbloggerkit.export.shareAsFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_clear_cache.setOnClickListener {
            MBIngressKit.clearLocalCache()
        }

        btn_register.setOnClickListener {
            val user = User(
                "",
                "",
                "Test",
                "Test1",
                "1970-01-01",
                "",
                "",
                "",
                "DE",
                "de-DE",
                "",
                "",
                UserPinStatus.UNKNOWN,
                Address("Test", "1", "12345", "fsdf", "DE",
                    null, null, null, null, null, null,
                    null, null, null, null),
                CommunicationPreference(false, false, true, false),
                UnitPreferences(
                    UnitPreferences.ClockHoursUnits.TYPE_24H,
                    UnitPreferences.SpeedDistanceUnits.KILOMETERS,
                    UnitPreferences.ConsumptionCoUnits.LITERS_PER_100_KILOMETERS,
                    UnitPreferences.ConsumptionEvUnits.KILOWATT_HOURS_PER_100_KILOMETERS,
                    UnitPreferences.ConsumptionGasUnits.KILOGRAM_PER_100_KILOMETERS,
                    UnitPreferences.TirePressureUnits.BAR,
                    UnitPreferences.TemperatureUnits.CELSIUS
                ),
                AccountIdentifier.UNKNOWN,
                "",
                "MR",
                "",
                null,
                false
            )
            MBIngressKit.userService().createUser(true, user)
                .onComplete {
                    MBLoggerKit.d("Create User completed $it")
                }
                .onFailure {
                    MBLoggerKit.e("Register User failed")
                    if (it?.requestError is UserInputErrors) {
                        val error = it.requestError as UserInputErrors
                        MBLoggerKit.d(error.errors.toString())
                    }
                }
        }
        btn_login.setOnClickListener {
            Toast.makeText(this@MainActivity, "Not supported yet", Toast.LENGTH_SHORT).show()
        }
        btn_login_native.setOnClickListener {
            showPendingLogin()
            // For testing, simply replace usercredentials here
            MBIngressKit.loginWithCredentials(UserCredentials(USER, "240560"))
                .onComplete {
                    showLoggedIn()
                    val token = MBIngressKit.authenticationService().getToken().jwtToken
                    MBLoggerKit.d("Login with UC completed $token")
                }.onFailure {
                    showLoggedOut()
                    val msg = if (it?.requestError is LoginFailure) {
                        "FAILED: ${(it.requestError as? LoginFailure)?.name}"
                    } else {
                        "FAILED: ${it?.requestError}"
                    }
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    MBLoggerKit.e(msg)
                }
        }
        btn_logout.setOnClickListener {
            showPendingLogout()
            MBIngressKit.logout()
                .onComplete {
                    showLoggedOut()
                }
        }
        btn_token_test.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete { token ->
                    MBLoggerKit.d("Refresh Token completed: $token")
                }.onFailure { throwable ->
                    MBLoggerKit.e("Refresh Token failed", throwable = throwable)
                }
        }
        btn_fetch_user.setOnClickListener {
            val token = MBIngressKit.authenticationService().getToken().jwtToken
            MBIngressKit.userService().loadUser(token.plainToken)
                .onComplete {
                    Toast.makeText(this@MainActivity, "$it", Toast.LENGTH_LONG).show()
                    MBLoggerKit.d("Load User completed $it")
                }.onFailure {
                    Toast.makeText(this@MainActivity, "$it?", Toast.LENGTH_LONG).show()
                }
        }

        btn_cached_user.setOnClickListener {
            val user = MBIngressKit.cachedUser()
            Toast.makeText(this, "$user", Toast.LENGTH_SHORT).show()
            MBLoggerKit.d("Cached user: $user")
        }

        btn_update_user.setOnClickListener {
            val token = MBIngressKit.authenticationService().getToken().jwtToken
            val ciamId = JWT(token.plainToken).getClaim("ciamid").asString().toString()
            MBIngressKit.userService().loadUser(ciamId)
                .onComplete { user ->
                    val newUser = user.copy(
                        address = Address(
                            "Teststreet",
                            "21",
                            "12345",
                            "City",
                            "DE",
                            "Saxony",
                            null,
                            null,
                            null,
                            "2",
                            null,
                            "Test1",
                            "Test2",
                            "Test3",
                            null
                        ),
                        title = "ABC",
                        salutationCode = "MR",
                        taxNumber = "123ABC"
                    )
                    MBIngressKit.userService().updateUser(token.plainToken, newUser)
                        .onComplete {
                            Toast.makeText(this@MainActivity, "$it", Toast.LENGTH_LONG).show()
                            MBLoggerKit.d("Update User completed: $it")
                        }
                        .onFailure {
                            Toast.makeText(this@MainActivity, "$it?", Toast.LENGTH_LONG).show()
                            MBLoggerKit.e("Update User failed")
                            MBLoggerKit.d(it?.requestError?.toString().orEmpty())
                        }
                }


        }

        btn_send_pin.setOnClickListener {
            val locale = Locale.getDefault()
            MBIngressKit.userService().sendPin(USER, locale.country.toUpperCase())
                .onComplete {
                    MBLoggerKit.d("Send pin for user ${it.userName}")
                }.onFailure {
                    if (it?.requestError is UserInputErrors) {
                        Toast.makeText(this, (it.requestError as? UserInputErrors)?.errors?.firstOrNull()?.description, Toast.LENGTH_SHORT).show()
                    }
                    MBLoggerKit.e("Failed to send pin: $it")
                }
        }

        btn_fetch_countries.setOnClickListener {
            MBIngressKit.userService().fetchCountries()
                .onComplete {
                    MBLoggerKit.d("Received countries: ${it.countries}")
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch countries.")
                    MBLoggerKit.e("NetworkError: ${it?.networkError}")
                    MBLoggerKit.e("RequestError: ${it?.requestError}")
                }
        }

        btn_fetch_natcon_countries.setOnClickListener {
            MBIngressKit.userService().fetchNatconCountries()
                .onComplete { MBLoggerKit.d("Received natcon countries: $it") }
                .onFailure { MBLoggerKit.e("Failed to fetch natcon countries.") }
        }

        btn_update_profile_picture.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.lego_avatar_small)

            val token = MBIngressKit.authenticationService().getToken().jwtToken
            val bitmapByteArray = bitmap.toByteArray(80)

            MBIngressKit.userService().updateProfilePicture(token.plainToken, bitmapByteArray, "image/jpeg")
                .onComplete {
                    MBLoggerKit.d("Profile Picture updated")
                }.onFailure {
                    MBLoggerKit.e("Failed to update Profile Picture")
                }
        }

        btn_fetch_profile_picture_bitmap.setOnClickListener {
            iv_avatar.setImageBitmap(null)
            val token = MBIngressKit.authenticationService().getToken().jwtToken
            MBIngressKit.userService().fetchProfilePictureBytes(token.plainToken)
                .onComplete {
                    iv_avatar.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                    MBLoggerKit.d("Received Profile Picture Bitmap")
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch Profile Picture Bitmap")
                }
        }

        btn_fetch_profile_picture_bytes.setOnClickListener {
            iv_avatar.setImageBitmap(null)
            val token = MBIngressKit.authenticationService().getToken().jwtToken
            MBIngressKit.userService().fetchProfilePictureBytes(token.plainToken)
                .onComplete {
                    BitmapFactory.decodeByteArray(it, 0, it.size)?.let { bitmap ->
                        iv_avatar.setImageBitmap(bitmap)
                    }
                    MBLoggerKit.d("Received Profile Picture Bytes. Size: ${it.size}")
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch Profile Picture Bytes")
                }
        }

        btn_set_pin.setOnClickListener {
            val token = MBIngressKit.authenticationService().getToken().jwtToken.plainToken
            MBIngressKit.userService().setPin(token, CURRENT_PIN)
                .onComplete {
                    MBLoggerKit.d("Set pin: $CURRENT_PIN")
                    Toast.makeText(this, "Set pin: $CURRENT_PIN", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    MBLoggerKit.e("Failed to set pin.")
                    Toast.makeText(this, "Failed to set pin.", Toast.LENGTH_SHORT).show()
                    MBLoggerKit.d(it?.requestError?.toString().orEmpty())
                }
        }
        btn_change_pin.setOnClickListener {
            val token = MBIngressKit.authenticationService().getToken().jwtToken.plainToken
            MBIngressKit.userService().changePin(token, CURRENT_PIN, NEW_PIN)
                .onComplete {
                    MBLoggerKit.d("Set pin: $CURRENT_PIN -> $NEW_PIN")
                    Toast.makeText(this, "Set pin: $CURRENT_PIN -> $NEW_PIN", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    MBLoggerKit.e("Failed to change pin.")
                    Toast.makeText(this, "Failed to change pin.", Toast.LENGTH_SHORT).show()
                    MBLoggerKit.d(it?.requestError?.toString().orEmpty())
                }
        }
        btn_delete_pin.setOnClickListener {
            val token = MBIngressKit.authenticationService().getToken().jwtToken.plainToken
            MBIngressKit.userService().deletePin(token, CURRENT_PIN)
                .onComplete {
                    MBLoggerKit.d("Deleted pin: $CURRENT_PIN")
                    Toast.makeText(this, "Deleted pin: $CURRENT_PIN", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    MBLoggerKit.e("Failed to delete pin.")
                    Toast.makeText(this, "Failed to delete pin.", Toast.LENGTH_SHORT).show()
                }
        }
        btn_reset_pin.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete { token ->
                    MBIngressKit.userService().resetPin(token.jwtToken.plainToken)
                        .onComplete {
                            MBLoggerKit.d("Reseted Pin")
                            Toast.makeText(this, "Reseted Pin", Toast.LENGTH_LONG).show()
                        }
                        .onFailure {
                            MBLoggerKit.e("Failed to reset pin.")
                            Toast.makeText(this, "Failed to reset pin.", Toast.LENGTH_SHORT).show()
                        }
                }
                .onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN)
                    Toast.makeText(this, FAILED_TO_REFRESH_TOKEN, Toast.LENGTH_SHORT).show()
                }
        }
        btn_send_biometric.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete {
                    MBIngressKit.userService().sendBiometricActivation(
                        it.jwtToken.plainToken, Locale.getDefault().country, UserBiometricState.ENABLED, CURRENT_PIN
                    ).onComplete {
                        MBLoggerKit.d("Sent biometric state.")
                        Toast.makeText(this, "Sent biometric state.", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        MBLoggerKit.e("Failed to send biometric state.")
                        Toast.makeText(this, "Failed to send biometric state.", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN)
                    Toast.makeText(this, FAILED_TO_REFRESH_TOKEN, Toast.LENGTH_SHORT).show()
                }
        }
        btn_send_unit_preferences.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete {
                    MBIngressKit.userService().updateUnitPreferences(
                        it.jwtToken.plainToken,
                        UnitPreferences(
                            UnitPreferences.ClockHoursUnits.TYPE_24H,
                            UnitPreferences.SpeedDistanceUnits.KILOMETERS,
                            UnitPreferences.ConsumptionCoUnits.LITERS_PER_100_KILOMETERS,
                            UnitPreferences.ConsumptionEvUnits.KILOWATT_HOURS_PER_100_KILOMETERS,
                            UnitPreferences.ConsumptionGasUnits.KILOGRAM_PER_100_KILOMETERS,
                            UnitPreferences.TirePressureUnits.BAR,
                            UnitPreferences.TemperatureUnits.CELSIUS
                        )
                    ).onComplete {
                        MBLoggerKit.d("Sent unit preferences.")
                        Toast.makeText(this, "Sent unit preferences.", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        MBLoggerKit.e("Failed to send unit preferences.")
                        Toast.makeText(this, "Failed to send unit preferences.", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN, throwable = it)
                    Toast.makeText(this, FAILED_TO_REFRESH_TOKEN, Toast.LENGTH_SHORT).show()
                }
        }
        btn_fetch_profile_fields.setOnClickListener {
            MBIngressKit.userService().fetchProfileFields(
                "DE",
                "en-US"
            ).onComplete { data ->
                MBLoggerKit.d("Fetched profile fields.")
                data.customerDataFields.forEach { field -> MBLoggerKit.d("$field") }
                data.fieldDependencies.forEach { field -> MBLoggerKit.d("$field") }
                data.groupDependencies.forEach { field -> MBLoggerKit.d("$field") }
                Toast.makeText(this, "Fetched profile fields.", Toast.LENGTH_SHORT).show()
                MBIngressKit.cachedProfileFields("DE", "en-US")?.let {
                    MBLoggerKit.d("Cached fields: $it")
                } ?: MBLoggerKit.e("No cached fields.")
            }.onFailure {
                MBLoggerKit.e("Failed to fetch profile fields.")
                Toast.makeText(this, "Failed to fetch profile fields.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_fetch_ciam_terms.setOnClickListener {
            MBIngressKit.userService().fetchCiamTermsAndConditions("DE", "en-US")
                .onComplete { agreements ->
                    agreements.agreements.forEach {
                        MBLoggerKit.d(it.displayName.orEmpty())
                    }
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch ciam agreements.")
                }
        }

        btn_fetch_soe_terms.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete { token ->
                    MBIngressKit.userService().fetchSOETermsAndConditions(token.jwtToken.plainToken, "DE", "en-US")
                        .onComplete { agreements ->
                            agreements.agreements.forEach {
                                MBLoggerKit.d(it.displayName.orEmpty())
                            }
                            if (agreements.agreements.isNotEmpty()) {
                                val path = agreements.agreements[0].filePath!!
                                val file = File(path)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(IngressFileProvider.getUriForFile(this, file), "application/pdf")
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                startActivity(intent)
                            }
                        }.onFailure {
                            MBLoggerKit.e("Failed to fetch SOE agreements.")
                        }
                }
                .onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN, throwable = it)
                }
        }

        btn_fetch_natcon_terms.setOnClickListener {
            MBIngressKit.userService().fetchNatconTermsAndConditions("MY", "en-US")
                .onComplete { agreements ->
                    agreements.agreements.forEach {
                        MBLoggerKit.d(it.text)
                    }
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch NATCON agreements.")
                }
        }

        btn_fetch_custom_terms.setOnClickListener {
            MBIngressKit.userService().fetchCustomTermsAndConditions("DE", null, "en-US")
                .onComplete { agreements ->
                    agreements.agreements.forEach {
                        MBLoggerKit.d(it.documentId)
                    }
                }.onFailure {
                    MBLoggerKit.e("Failed to fetch Custom agreements.")
                }
        }

        btn_update_ciam.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete { token ->
                    MBIngressKit.userService().fetchCiamTermsAndConditions("DE", "de-DE")
                        .onComplete { agreements ->
                            MBIngressKit.userService().updateCiamAgreements(
                                token.jwtToken.plainToken,
                                UserAgreementUpdates("DE", agreements.agreements.map {
                                    UserAgreementUpdate(it.documentId, it.documentVersion.orEmpty(), it.locale, true)
                                }),
                                "de-DE"
                            ).onComplete {
                                MBLoggerKit.d("Updated CIAM agreements.")
                            }.onFailure {
                                MBLoggerKit.e("Failed to update CIAM agreements.")
                            }
                        }.onFailure {
                            MBLoggerKit.e("Failed to fetch CIAM agreements.")
                        }
                }.onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN)
                }
        }

        btn_update_adaption_values.setOnClickListener {
            MBIngressKit.refreshTokenIfRequired()
                .onComplete { token ->
                    MBIngressKit.userService().loadUser(token.jwtToken.plainToken)
                        .onComplete { user ->
                            val bodyHeight = user.bodyHeight?.copy(bodyHeight = 200)
                                ?: UserBodyHeight(200, false)
                            MBIngressKit.userService().updateAdaptionValues(token.jwtToken.plainToken, bodyHeight)
                                .onComplete {
                                    MBLoggerKit.d("Updated adaption values.")
                                }.onFailure {
                                    MBLoggerKit.e("Failed to update adaption values.")
                                }
                        }.onFailure {
                            MBLoggerKit.e("Failed to fetch user.")
                        }
                }.onFailure {
                    MBLoggerKit.e(FAILED_TO_REFRESH_TOKEN)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (MBIngressKit.authenticationService().getTokenState() !is TokenState.LOGGEDOUT) {
            showLoggedIn()
        } else {
            showLoggedOut()
        }
    }

    private fun Bitmap.toByteArray(jpegQuality: Int): ByteArray {
        return ByteArrayOutputStream().also {
            compress(Bitmap.CompressFormat.JPEG, jpegQuality, it)
        }.toByteArray()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh_token -> {
                repeat(10) {
                    Handler().postDelayed({
                        MBLoggerKit.d("Putting $it")
                        refreshToken(it + 1)
                    }, it * 500L)
                }
            }
            R.id.force_token_refresh -> MBIngressKit.authenticationService().forceTokenRefresh()
            R.id.share_file -> MBLoggerKit.loadCurrentLog().shareAsFile(this, "MBLogin.log")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshToken(counter: Int) {
        val task = MBIngressKit.refreshTokenIfRequired()
        task.onComplete {
            Toast.makeText(this, "Refresh Success: ${it.accessToken}", Toast.LENGTH_LONG).show()
            val token = it.jwtToken.plainToken
            MBLoggerKit.d("Completed $counter. (${token.substringOfLast(5)})")
        }.onFailure {
            Toast.makeText(this, "Refresh Failed: ${it?.message
                ?: "Unknown"}", Toast.LENGTH_LONG).show()
            MBLoggerKit.e("$counter failed.", throwable = it)
        }
    }

    private fun showPendingLogin() {
        btn_login.visibility = View.GONE
        btn_login_native.visibility = View.GONE
        tv_login_started.visibility = View.VISIBLE
        tv_logout_started.visibility = View.GONE
        btn_logout.visibility = View.GONE
    }

    private fun showLoggedIn() {
        btn_login.visibility = View.GONE
        btn_login_native.visibility = View.GONE
        tv_login_started.visibility = View.GONE
        btn_logout.visibility = View.VISIBLE
        tv_logout_started.visibility = View.GONE
    }

    private fun showLoggedOut() {
        btn_login_native.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
        tv_login_started.visibility = View.GONE
        btn_logout.visibility = View.GONE
        tv_logout_started.visibility = View.GONE
    }

    private fun showPendingLogout() {
        btn_login_native.visibility = View.GONE
        btn_login.visibility = View.GONE
        tv_login_started.visibility = View.GONE
        btn_logout.visibility = View.GONE
        tv_logout_started.visibility = View.VISIBLE
    }

    private fun String.substringOfLast(amount: Int) =
        if (amount >= length) this else substring(length - amount)

    companion object {
        private const val USER = ""
        private const val CURRENT_PIN = "1234"
        private const val NEW_PIN = "7890"
        private const val FAILED_TO_REFRESH_TOKEN = "Failed to refresh token."
    }
}
