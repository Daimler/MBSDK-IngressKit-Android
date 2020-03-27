package com.daimler.mbingresskit.ingress

interface CiamUrlFactory {
    // todo: check if endpoints are fix or if they can be changed while using mblogin module
    fun createConfig(configName: String): Endpoint
    fun getConfigNames(): Array<String>
}