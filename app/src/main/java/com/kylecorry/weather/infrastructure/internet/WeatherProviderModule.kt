package com.kylecorry.weather.infrastructure.internet

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WeatherProviderModule {

    @Provides
    @Singleton
    fun provideWeatherProvider(): WeatherProvider {
        return OpenMeteoWeatherProvider()
    }

}