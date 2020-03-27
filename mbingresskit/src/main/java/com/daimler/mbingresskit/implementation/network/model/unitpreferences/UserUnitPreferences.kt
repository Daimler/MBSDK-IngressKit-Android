package com.daimler.mbingresskit.implementation.network.model.unitpreferences

import com.daimler.mbingresskit.common.UnitPreferences
import com.google.gson.annotations.SerializedName

data class UserUnitPreferences(
    @SerializedName("clockHours") val clockHours: UnitPreferences.ClockHoursUnits,
    @SerializedName("speedDistance") val speedDistance: UnitPreferences.SpeedDistanceUnits,
    @SerializedName("consumptionCo") val consumptionCo: UnitPreferences.ConsumptionCoUnits,
    @SerializedName("consumptionEv") val consumptionEv: UnitPreferences.ConsumptionEvUnits,
    @SerializedName("consumptionGas") val consumptionGas: UnitPreferences.ConsumptionGasUnits,
    @SerializedName("tirePressure") val tirePressure: UnitPreferences.TirePressureUnits,
    @SerializedName("temperature") val temperature: UnitPreferences.TemperatureUnits
)