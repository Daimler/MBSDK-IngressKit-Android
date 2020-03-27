package com.daimler.mbingresskit.ingress

interface CiamEndpointRepository {
    fun saveEnvironment(endpoint: CiamEndpoint)
    fun loadEndpoint(): CiamEndpoint
}