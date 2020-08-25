package adameapps.speedplayer.player

import adameapps.speedplayer.model.MusicLibrary
import adameapps.speedplayer.model.State
import android.app.Activity

class MusicManager(
    private val activity: Activity,
    musicPlaybackListener: MusicPlaybackListener,
    var mediumMusicThresholdSpeed: Int,
    var highMusicThresholdSpeed: Int
) : PlaybackDataChangeListener {
    private val musicPlayer: MusicPlayer = MusicPlayer(activity, musicPlaybackListener, this)

    private var currentState = State.NONE
    private var lowSpeedTrack = 0
    private var mediumSpeedTrack = 0
    private var highSpeedTrack = 0
    private var lowSpeedTimestamp = 0
    private var mediumSpeedTimestamp = 0
    private var highSpeedTimestamp = 0

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

    fun isPlaying() = currentState != State.NONE

    fun reportSpeed(speed: Int) {
        if (isPlaying()) {
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

    fun assignMusicPlaybackListener(musicPlaybackListener: MusicPlaybackListener) {
        musicPlayer.assignMusicPlaybackListener(musicPlaybackListener)
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
            val track: Int
            val timestamp: Int
            when (state) {
                State.LOW -> {
                    track = lowSpeedTrack
                    timestamp = lowSpeedTimestamp
                }
                State.MEDIUM -> {
                    track = mediumSpeedTrack
                    timestamp = mediumSpeedTimestamp
                }
                State.HIGH -> {
                    track = highSpeedTrack
                    timestamp = highSpeedTimestamp
                }
                else -> {
                    track = 0
                    timestamp = 0
                }
            }
            if (isPlaying()) {
                musicPlayer.fade(MusicLibrary.getMusic(activity, state), track, timestamp)
            } else {
                musicPlayer.play(MusicLibrary.getMusic(activity, state), track)
            }
            currentState = state
        }
    }

    override fun onTrackFinished() {
        when (currentState) {
            State.LOW -> {
                lowSpeedTrack++
                if (lowSpeedTrack >= MusicLibrary.getMusic(activity, currentState).size) {
                    lowSpeedTrack = 0
                }
            }
            State.MEDIUM -> {
                mediumSpeedTrack++
                if (mediumSpeedTrack >= MusicLibrary.getMusic(activity, currentState).size) {
                    mediumSpeedTrack = 0
                }
            }
            State.HIGH -> {
                highSpeedTrack++
                if (highSpeedTrack >= MusicLibrary.getMusic(activity, currentState).size) {
                    highSpeedTrack = 0
                }
            }
            else -> {
            }
        }
    }
}
