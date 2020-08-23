package adameapps.speedplayer.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetupViewModel : ViewModel() {
    private val _mediumThresholdInfo = MutableLiveData<String>()
    val mediumThresholdInfo: LiveData<String> = _mediumThresholdInfo

    private val _highThresholdInfo = MutableLiveData<String>()
    val highThresholdInfo: LiveData<String> = _highThresholdInfo

    fun updateMediumThreshold(threshold: Int) {
        _mediumThresholdInfo.value = "CURRENT MEDIUM: $threshold"
    }

    fun updateHighThreshold(threshold: Int) {
        _highThresholdInfo.value = "CURRENT HIGH: $threshold"
    }
}
