package com.daimler.mbingresskit.ingress

import com.daimler.mbingresskit.common.AuthPrompt
import com.daimler.mbingresskit.common.AuthScope

class CiamConfig @JvmOverloads constructor(
    val endpoint: Endpoint,
    val ciamIds: CiamIds,
    authScope: AuthScope = AuthScope(),
    authPrompt: AuthPrompt = AuthPrompt()
) {

    val scope: String = authScope.formattedString

    val prompt: String = authPrompt.formattedString
}