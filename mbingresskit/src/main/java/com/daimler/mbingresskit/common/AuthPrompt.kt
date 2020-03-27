package com.daimler.mbingresskit.common

class AuthPrompt(
    vararg prompt: Prompt
) {

    val formattedString = asFormatedString(prompt)

    override fun toString(): String = formattedString

    private fun asFormatedString(scope: Array<out Prompt>): String = scope
            .map { it.value }
            .filter { it.isNotEmpty() }
            .distinct()
            .joinToString(" ")
}