package com.daimler.mbingresskit.ingress

class EndpointCiamUrlFactory : CiamUrlFactory {

    override fun createConfig(configName: String): Endpoint = CiamEnvironment.valueOf(configName)

    override fun getConfigNames(): Array<String> = CiamEnvironment.values()
            .map { it.name }
            .toTypedArray()
}