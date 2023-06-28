package com.kylecorry.weather.infrastructure.location

import android.content.Context
import android.location.Geocoder
import com.kylecorry.andromeda.location.GPS
import com.kylecorry.andromeda.location.NetworkGPS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): LocationService {
        val gps = NetworkGPS(context)
        return GpsLocationService(gps, Geocoder(context))
    }

}