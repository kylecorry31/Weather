package com.kylecorry.weather.infrastructure.location

import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.kylecorry.andromeda.core.coroutines.onIO
import com.kylecorry.andromeda.location.IGPS
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.weather.domain.City
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GpsLocationService @Inject constructor(
    private val gps: IGPS,
    private val geocoder: Geocoder
) :
    LocationService {

    override fun getLastLocation(): Coordinate? {
        if (gps.location == Coordinate.zero) {
            return null
        }
        return gps.location
    }

    override suspend fun getLocation(): Coordinate? = onIO {
        gps.read()
        getLastLocation()
    }

    override suspend fun getCity(location: Coordinate): City? = onIO {
        suspendCoroutine { cont ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    5,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            cont.resume(getCity(addresses))
                        }

                        override fun onError(errorMessage: String?) {
                            cont.resume(null)
                        }
                    })
            } else {
                val result = geocoder.getFromLocation(location.latitude, location.longitude, 5)
                if (result == null) {
                    cont.resume(null)
                } else {
                    cont.resume(getCity(result))
                }
            }
        }
    }

    private fun getCity(addresses: List<Address>): City? {
        val address = addresses.firstOrNull { it.locality != null && it.adminArea != null } ?: return null
        return City(address.locality, address.adminArea)
    }
}