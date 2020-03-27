package com.daimler.mbingresskit.implementation.network.model.pin

import com.daimler.mbnetworkkit.networking.RequestError

data class PinErrors(val errors: List<PinError>) : RequestError