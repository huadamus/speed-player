package adameapps.speedplayer.gps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

@SuppressLint("MissingPermission")
class GPSManager(context: Context, private val speedChangeListener: SpeedChangeListener) :
    LocationListener {

    private val locationManager: LocationManager =
        context.getSystemService(LOCATION_SERVICE) as LocationManager

    init {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this
        )
    }

    override fun onLocationChanged(location: Location) {
        speedChangeListener.onSpeedChange(getKilometersPerHourFromMetersPerSecond(location.speed))
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
    }

    private fun getKilometersPerHourFromMetersPerSecond(speed: Float): Int {
        return (speed * METERS_PER_SECOND_KILOMETERS_PER_HOUR_DIVIDER).toInt()
    }

    companion object {
        private const val MINIMUM_TIME_BETWEEN_UPDATES = 1000L
        private const val MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1.0f
        private const val METERS_PER_SECOND_KILOMETERS_PER_HOUR_DIVIDER = 18.0f / 5.0f
    }
}
