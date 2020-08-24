package adameapps.speedplayer.data

import adameapps.speedplayer.DEFAULT_HIGH_THRESHOLD
import adameapps.speedplayer.DEFAULT_MEDIUM_THRESHOLD
import adameapps.speedplayer.model.MusicFile
import adameapps.speedplayer.model.MusicLibrary.musicList
import adameapps.speedplayer.model.State
import android.app.Activity
import android.content.Context

object DataManager {

    private const val MEDIUM_STATE_THRESHOLD_TAG = "medium_threshold"
    private const val HIGH_STATE_THRESHOLD_TAG = "high_threshold"

    fun saveTrackType(activity: Activity, musicFile: MusicFile, state: State) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(musicFile.uri.path, state.id)
            commit()
        }
    }

    fun readTracksListByState(
        activity: Activity,
        state: State
    ): List<MusicFile> {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return listOf()
        return musicList.filter { track ->
            sharedPref.getInt(
                track.uri.path,
                State.NONE.id
            ) == state.id
        }
    }

    fun readTrackType(activity: Activity, musicFile: MusicFile): State {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return State.NONE
        return State.fromInt(sharedPref.getInt(musicFile.uri.path, State.NONE.id))
    }

    fun readAnyTrackSelectedForEachState(activity: Activity): Boolean {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return false
        return musicList.any { track ->
            sharedPref.getInt(
                track.uri.path,
                State.NONE.id
            ) == State.LOW.id
        } && musicList.any { track ->
            sharedPref.getInt(
                track.uri.path,
                State.NONE.id
            ) == State.MEDIUM.id
        } && musicList.any { track ->
            sharedPref.getInt(
                track.uri.path,
                State.NONE.id
            ) == State.HIGH.id
        }
    }

    fun readMediumThreshold(activity: Activity): Int {
        return readThreshold(activity, false)
    }

    fun readHighThreshold(activity: Activity): Int {
        return readThreshold(activity, true)
    }

    private fun readThreshold(activity: Activity, high: Boolean): Int {
        val tag: String
        val defaultThreshold: Int
        if (high) {
            tag = HIGH_STATE_THRESHOLD_TAG
            defaultThreshold = DEFAULT_HIGH_THRESHOLD
        } else {
            tag = MEDIUM_STATE_THRESHOLD_TAG
            defaultThreshold = DEFAULT_MEDIUM_THRESHOLD
        }
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return defaultThreshold
        return sharedPref.getInt(tag, defaultThreshold)
    }

    fun saveMediumThreshold(activity: Activity, threshold: Int) {
        saveThreshold(activity, threshold, false)
    }

    fun saveHighThreshold(activity: Activity, threshold: Int) {
        saveThreshold(activity, threshold, true)
    }

    private fun saveThreshold(activity: Activity, threshold: Int, high: Boolean) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val prefName = if (high) {
            HIGH_STATE_THRESHOLD_TAG
        } else {
            MEDIUM_STATE_THRESHOLD_TAG
        }
        with(sharedPref.edit()) {
            putInt(prefName, threshold)
            commit()
        }
    }
}
