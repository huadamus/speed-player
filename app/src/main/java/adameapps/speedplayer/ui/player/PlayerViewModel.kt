package adameapps.speedplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel : ViewModel() {

    private val _speedMeter = MutableLiveData<String>().apply {
        value = ""
    }
    val speedMeter: LiveData<String> = _speedMeter
    private val _trackTitle = MutableLiveData<String>().apply {
        value = ""
    }
    val trackTitle: LiveData<String> = _trackTitle

    private var lastSpeed = 0

    fun onSpeedChange(playing: Boolean, speedInKilometersPerHour: Int) {
        lastSpeed = speedInKilometersPerHour
        if(playing) {
            _speedMeter.value = lastSpeed.toString() + "km/h"
        }
    }

    fun showTrackTitle(title: String) {
        _trackTitle.value = title
    }

    fun showPlayerTurnedOff() {
        _speedMeter.value = ""
        _trackTitle.value = ""
    }

    fun onGpsEnable() {
        _speedMeter.value = "WAITING FOR SPEED TRACKING..."
    }

    fun onGpsDisable() {
        _speedMeter.value = "GPS DISABLED"
    }
}
