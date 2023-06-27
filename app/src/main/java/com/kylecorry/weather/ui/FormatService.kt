package com.kylecorry.weather.ui

import android.content.Context
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import com.kylecorry.sol.science.meteorology.WeatherCondition
import com.kylecorry.weather.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormatService @Inject constructor(@ApplicationContext private val context: Context) {
    fun formatDate(
        date: ZonedDateTime,
        includeWeekDay: Boolean = true,
        abbreviateMonth: Boolean = false
    ): String {
        return DateUtils.formatDateTime(
            context,
            date.toEpochSecond() * 1000,
            DateUtils.FORMAT_SHOW_DATE or (if (includeWeekDay) DateUtils.FORMAT_SHOW_WEEKDAY else 0) or DateUtils.FORMAT_SHOW_YEAR or (if (abbreviateMonth) DateUtils.FORMAT_ABBREV_MONTH else 0)
        )
    }

    @DrawableRes
    fun getWeatherImage(condition: WeatherCondition?): Int {
        return when (condition) {
            WeatherCondition.Clear -> R.drawable.sunny
            WeatherCondition.Overcast -> R.drawable.cloudy
            WeatherCondition.Precipitation -> R.drawable.ic_precipitation
            WeatherCondition.Storm -> R.drawable.storm
            WeatherCondition.Wind -> R.drawable.wind
            WeatherCondition.Rain -> R.drawable.light_rain
            WeatherCondition.Snow -> R.drawable.ic_precipitation_snow
            WeatherCondition.Thunderstorm -> R.drawable.storm
            null -> R.drawable.steady
        }
    }

    fun formatWeather(condition: WeatherCondition?): String {
        return when (condition) {
            WeatherCondition.Clear -> context.getString(R.string.weather_clear)
            WeatherCondition.Overcast -> context.getString(R.string.weather_overcast)
            WeatherCondition.Precipitation -> context.getString(R.string.weather_precipitation)
            WeatherCondition.Storm -> context.getString(R.string.weather_storm)
            WeatherCondition.Wind -> context.getString(R.string.weather_wind)
            WeatherCondition.Rain -> context.getString(R.string.precipitation_rain)
            WeatherCondition.Snow -> context.getString(R.string.precipitation_snow)
            WeatherCondition.Thunderstorm -> context.getString(R.string.weather_thunderstorm)
            null -> context.getString(R.string.weather_no_change)
        }
    }

}