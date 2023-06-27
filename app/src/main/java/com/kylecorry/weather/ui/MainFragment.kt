package com.kylecorry.weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.core.system.Intents
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.core.ui.Colors.withAlpha
import com.kylecorry.andromeda.core.ui.setCompoundDrawables
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.ceres.chart.Chart
import com.kylecorry.ceres.chart.data.AreaChartLayer
import com.kylecorry.ceres.chart.data.BitmapChartLayer
import com.kylecorry.sol.time.Time.toZonedDateTime
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.TemperatureUnits
import com.kylecorry.weather.R
import com.kylecorry.weather.databinding.FragmentMainBinding
import com.kylecorry.weather.infrastructure.internet.WeatherProvider
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : BoundFragment<FragmentMainBinding>() {

    @Inject
    lateinit var formatter: FormatService

    @Inject
    lateinit var weatherProvider: WeatherProvider

    private val temperatureLayer by lazy {
        AreaChartLayer(
            emptyList(),
            Resources.color(requireContext(), R.color.primary_40).withAlpha(100),
            Resources.color(requireContext(), R.color.primary_40).withAlpha(50)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.weatherChart.configureYAxis(labelCount = 5)
        binding.weatherChart.plot(temperatureLayer)
        binding.dataSource.setOnClickListener {
            startActivity(Intents.url("https://open-meteo.com/"))
        }
    }

    override fun onResume() {
        super.onResume()
        inBackground {
            val weather = weatherProvider.getWeather(Coordinate(42.0, -72.0))
            val current = weather.minByOrNull { Duration.between(Instant.now(), it.time).abs() }
            onMain {
                binding.homeTitle.title.text = formatter.formatWeather(current?.value?.condition)
                binding.homeTitle.title.setCompoundDrawables(
                    Resources.dp(requireContext(), 24f).toInt(),
                    left = formatter.getWeatherImage(current?.value?.condition)
                )
                binding.homeTitle.subtitle.text =
                    "${current?.value?.temperature?.convertTo(TemperatureUnits.F)?.temperature?.roundToInt() ?: 0}°    ${current?.value?.humidity?.roundToInt() ?: 0}%"
                temperatureLayer.data = Chart.getDataFromReadings(weather) {
                    it.temperature.convertTo(TemperatureUnits.F).temperature
                }
            }
        }
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater, container, false)
    }
}