package adameapps.speedplayer.player

import adameapps.speedplayer.model.MusicLibrary
import adameapps.speedplayer.model.State
import android.app.Activity

class MusicManager(
    private val activity: Activity,
    musicPlaybackListener: MusicPlaybackListener,
    var mediumMusicThresholdSpeed: Int,
    var highMusicThresholdSpeed: Int
) {
    private val musicPlayer: MusicPlayer = MusicPlayer(activity, musicPlaybackListener)

    private var lowSpeedTimestamp = 0
    private var mediumSpeedTimestamp = 0
    private var highSpeedTimestamp = 0

    private var currentState = State.NONE

    fun start() {
        play(State.LOW)
        currentState = State.LOW
    }

    fun stop() {
        if (isPlaying()) {
            musicPlayer.stop()
        }
        currentState = State.NONE
    }

    fun terminate() {
        musicPlayer.terminate()
    }

    fun isPlaying() = currentState != State.NONE

    fun reportSpeed(speed: Int) {
        if(isPlaying()) {
            if (speed >= highMusicThresholdSpeed) {
                if (currentState != State.HIGH) {
                    play(State.HIGH)
                }
            } else if (speed >= mediumMusicThresholdSpeed) {
                if (currentState != State.MEDIUM) {
                    play(State.MEDIUM)
                }
            } else {
                if (currentState != State.LOW) {
                    play(State.LOW)
                }
            }
        }
    }

    private fun play(state: State) {
        if (currentState != state) {
            when (currentState) {
                State.NONE -> {
                }
                State.LOW -> lowSpeedTimestamp = musicPlayer.getTimestamp()
                State.MEDIUM -> mediumSpeedTimestamp = musicPlayer.getTimestamp()
                State.HIGH -> highSpeedTimestamp = musicPlayer.getTimestamp()
            }
            if (isPlaying()) {
                val timestamp = when(state) {
                    State.LOW -> lowSpeedTimestamp
                    State.MEDIUM -> mediumSpeedTimestamp
                    State.HIGH -> highSpeedTimestamp
                    else -> 0
                }
                musicPlayer.fade(MusicLibrary.getMusic(activity, state), 0, timestamp)
            } else {
                musicPlayer.play(MusicLibrary.getMusic(activity, state), 0)
            }
            currentState = state
        }
    }
}
