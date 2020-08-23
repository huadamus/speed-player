package adameapps.speedplayer.model

import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.io.MusicImporter
import android.app.Activity
import android.content.Context

object MusicLibrary {

    lateinit var musicList: List<MusicFile>
        private set

    fun init(context: Context) {
        musicList = MusicImporter.getMusic(context)
    }

    fun getMusic(activity: Activity, state: State): List<MusicFile> {
        return DataManager.readTracksListByState(activity, state)
    }
}
