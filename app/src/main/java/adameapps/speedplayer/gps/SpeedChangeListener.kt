package adameapps.speedplayer.gps

interface SpeedChangeListener {
    fun onSpeedChange(speedInKilometersPerHour: Int)
    fun onGpsEnable()
    fun onGpsDisable()
}
