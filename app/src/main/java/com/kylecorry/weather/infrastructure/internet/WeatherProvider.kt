package com.kylecorry.weather.infrastructure.internet

import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.Reading
import com.kylecorry.weather.domain.Weather

interface WeatherProvider {
    suspend fun getWeather(location: Coordinate): List<Reading<Weather>>
}