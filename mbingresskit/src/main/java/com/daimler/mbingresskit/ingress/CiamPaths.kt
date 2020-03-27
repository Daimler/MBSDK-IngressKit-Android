package com.daimler.mbingresskit.ingress

enum class CiamPaths(val pathValue: String) {
    AUTHORIZE("oidc10/auth/oauth/v2/authorize"),
    TOKEN("oidc10/auth/oauth/v2/token"),
    REGISTRATION("profile/register"),
    CONFIRMREGISTRATION("profile/confirm-registration"),
    PASSWORD("profile/set-password"),
    LOGOUT("ciam/logout"),
    CONTENTMANAGEMENT("profile/edit/apps"),
    NATIVE_REDIRECT("native/login-android-al"),
    NATIVE_CALLBACK("native/callback-android-al"),
    NATIVE_LOGOUT("native/logout-android-al")
}
