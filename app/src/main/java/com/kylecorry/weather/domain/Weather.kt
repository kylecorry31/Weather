package com.kylecorry.weather.domain

import com.kylecorry.sol.science.meteorology.WeatherCondition
import com.kylecorry.sol.units.Bearing
import com.kylecorry.sol.units.Pressure
import com.kylecorry.sol.units.Speed
import com.kylecorry.sol.units.Temperature

data class Weather(
    val condition: WeatherCondition?,
    val temperature: Temperature,
    val feelsLike: Temperature,
    val windSpeed: Speed,
    val windDirection: Bearing,
    val humidity: Float,
    val seaLevelPressure: Pressure,
    val precipitationChance: Float,
    val uvIndex: Int
)