package com.kylecorry.weather.infrastructure.location

import com.kylecorry.sol.units.Coordinate
import com.kylecorry.weather.domain.City

interface LocationService {
    fun getLastLocation(): Coordinate?
    suspend fun getLocation(): Coordinate?
    suspend fun getCity(location: Coordinate): City?
}