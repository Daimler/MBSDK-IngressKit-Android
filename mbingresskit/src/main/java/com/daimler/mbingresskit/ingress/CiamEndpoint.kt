package com.daimler.mbingresskit.ingress

import com.daimler.mbingresskit.common.AuthPrompt
import com.daimler.mbingresskit.common.AuthScope

data class CiamEndpoint(
    val environment: CiamEnvironment,
    val authScope: AuthScope,
    val authPrompt: AuthPrompt
)