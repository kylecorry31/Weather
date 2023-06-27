package com.kylecorry.weather.infrastructure.internet

import com.kylecorry.andromeda.core.coroutines.onIO
import com.kylecorry.sol.science.meteorology.WeatherCondition
import com.kylecorry.sol.units.Bearing
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.DistanceUnits
import com.kylecorry.sol.units.Pressure
import com.kylecorry.sol.units.Reading
import com.kylecorry.sol.units.Speed
import com.kylecorry.sol.units.Temperature
import com.kylecorry.sol.units.TimeUnits
import com.kylecorry.weather.domain.Weather
import com.openmeteo.api.Forecast
import com.openmeteo.api.common.time.Date
import com.openmeteo.api.common.time.Timezone
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class OpenMeteoWeatherProvider : WeatherProvider {
    override suspend fun getWeather(location: Coordinate): List<Reading<Weather>> = onIO {
        val query = Forecast.Query(
            latitude = location.latitude.toFloat(),
            longitude = location.longitude.toFloat(),
            hourly = Forecast.Hourly {
                listOf(
                    weathercode,
                    temperature2m,
                    apparentTemperature,
                    windspeed10m,
                    winddirection10m,
                    relativehumidity2m,
                    pressureMsl,
                    precipitationProbability,
                    uvIndex
                )
            },
            startDate = Date(LocalDate.now().toString()),
            endDate = Date(LocalDate.now().plusDays(1).toString()),
            timezone = Timezone.getTimeZone(ZoneId.systemDefault())
        )

        val forecast = Forecast(query).getOrThrow()

        val weather = mutableListOf<Reading<Weather>>()

        for (i in forecast.hourlyValues["time"]!!.indices) {

            val getValue = { key: String, default: Double ->
                forecast.hourlyValues[key]?.get(i) ?: default
            }

            val getFloat = { key: String, default: Float ->
                forecast.hourlyValues[key]?.get(i)?.toFloat() ?: default
            }

            weather.add(
                Reading(
                    Weather(
                        condition = mapWeatherCondition(
                            getValue(Forecast.Hourly.weathercode, 0.0).toInt()
                        ),
                        temperature = Temperature.celsius(
                            getFloat(Forecast.Hourly.temperature2m, 0f)
                        ),
                        feelsLike = Temperature.celsius(
                            getFloat(Forecast.Hourly.apparentTemperature, 0f)
                        ),
                        windSpeed = Speed(
                            getFloat(Forecast.Hourly.windspeed10m, 0f),
                            DistanceUnits.Kilometers,
                            TimeUnits.Hours
                        ),
                        windDirection = Bearing(
                            getFloat(Forecast.Hourly.winddirection10m, 0f)
                        ),
                        humidity = getFloat(Forecast.Hourly.relativehumidity2m, 0f),
                        seaLevelPressure = Pressure.hpa(
                            getFloat(Forecast.Hourly.pressureMsl, 0f)
                        ),
                        precipitationChance = getFloat(
                            Forecast.Hourly.precipitationProbability,
                            0f
                        ),
                        uvIndex = getFloat(Forecast.Hourly.uvIndex, 0f).toInt()
                    ),
                    Instant.ofEpochMilli(getValue("time", 0.0).toLong() * 1000L)
                )
            )
        }

        weather.sortedBy { it.time }.filter {
            it.time.isAfter(Instant.now()) && it.time.isBefore(
                Instant.now().plus(
                    Duration.ofDays(1)
                )
            )
        }
    }

    private fun mapWeatherCondition(code: Int): WeatherCondition? {
        /**
         * WMO weather codes
         * 0	Clear sky
        1, 2, 3	Mainly clear, partly cloudy, and overcast
        45, 48	Fog and depositing rime fog
        51, 53, 55	Drizzle: Light, moderate, and dense intensity
        56, 57	Freezing Drizzle: Light and dense intensity
        61, 63, 65	Rain: Slight, moderate and heavy intensity
        66, 67	Freezing Rain: Light and heavy intensity
        71, 73, 75	Snow fall: Slight, moderate, and heavy intensity
        77	Snow grains
        80, 81, 82	Rain showers: Slight, moderate, and violent
        85, 86	Snow showers slight and heavy
        95 *	Thunderstorm: Slight or moderate
        96, 99 *	Thunderstorm with slight and heavy hail
         */
        return when (code) {
            0, 1 -> WeatherCondition.Clear
            2, 3, 45, 48 -> WeatherCondition.Overcast
            51, 53, 55, 56, 57, 61, 63, 65, 80, 81, 82 -> WeatherCondition.Rain
            66, 67, 71, 73, 75, 77, 85, 86 -> WeatherCondition.Snow
            95, 96, 99 -> WeatherCondition.Thunderstorm
            else -> null
        }
    }
}