package com.daimler.mbingresskit.common

/**
 * Describes the optional prompt modes supported by CIAM for login and consent management
 */
enum class Prompt(val value: String) {
    /**
     * Do not show a login prompt
     */
    NONE("none"),
    /**
     * Always ask for user authentication even if the SSO is still valid
     */
    LOGIN("login"),
    /**
     * Ask also for consent to additional scopes
     */
    CONSENT("consent")
}