package adameapps.speedplayer.player

import adameapps.speedplayer.model.MusicFile
import adameapps.speedplayer.ui.PlayerService
import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class MusicPlayer(
    private val context: Context,
    private var musicPlaybackListener: MusicPlaybackListener,
    private val playbackDataChangeListener: PlaybackDataChangeListener
) {
    private var mediaPlayer = MediaPlayer()
    private var fading = false

    fun play(music: List<MusicFile>, currentTrackId: Int) {
        musicPlaybackListener.onMusicTitleSwitch(music[currentTrackId].title)
        try {
            mediaPlayer.setDataSource(context, music[currentTrackId].uri)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            musicPlaybackListener.onMusicPlaybackFailure()
            e.printStackTrace()
        }
        mediaPlayer.start()
        setMusicContinuityAndInterface(music, currentTrackId)
    }

    fun fade(music: List<MusicFile>, currentTrackId: Int, timestamp: Int) {
        if (!fading) {
            fading = true
            musicPlaybackListener.onMusicTitleSwitch(music[currentTrackId].title)
            val fadeMediaPlayer = MediaPlayer()
            try {
                fadeMediaPlayer.setDataSource(context, music[currentTrackId].uri)
                fadeMediaPlayer.prepare()
            } catch (e: IOException) {
                musicPlaybackListener.onMusicPlaybackFailure()
                e.printStackTrace()
            }
            fadeMediaPlayer.setVolume(SILENT_VOLUME, SILENT_VOLUME)
            fadeMediaPlayer.seekTo(timestamp)
            fadeMediaPlayer.start()
            GlobalScope.launch {
                var volume = PROPER_VOLUME
                while (volume > SILENT_VOLUME) {
                    mediaPlayer.setVolume(volume, volume)
                    fadeMediaPlayer.setVolume(PROPER_VOLUME - volume, PROPER_VOLUME - volume)
                    volume += VOLUME_CHANGE_PER_TICK
                    delay(MILLISECONDS_BETWEEN_TICKS)
                    if (!mediaPlayer.isPlaying) {
                        fadeMediaPlayer.stop()
                        fadeMediaPlayer.reset()
                        fadeMediaPlayer.release()
                        break
                    }
                }
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                    mediaPlayer.release()
                    mediaPlayer = fadeMediaPlayer
                    setMusicContinuityAndInterface(music, currentTrackId)
                }
            }
            fading = false
        }
    }

    fun stop() {
        musicPlaybackListener.onMusicTitleSwitch(EMPTY_TITLE)
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    fun getTimestamp() = mediaPlayer.currentPosition

    fun assignMusicPlaybackListener(musicPlaybackListener: MusicPlaybackListener) {
        this.musicPlaybackListener = musicPlaybackListener
    }

    private fun setMusicContinuityAndInterface(
        music: List<MusicFile>,
        currentTrackId: Int
    ) {
        mediaPlayer.setOnCompletionListener {
            playbackDataChangeListener.onTrackFinished()
            stop()
            val nextTrackId = if (currentTrackId + 1 == music.size) {
                0
            } else {
                currentTrackId + 1
            }
            play(music, nextTrackId)
        }
        PlayerService.updateNotification(context, music[currentTrackId].title)
    }

    companion object {
        const val EMPTY_TITLE = ""
        const val PROPER_VOLUME = 1.0f
        const val SILENT_VOLUME = 0.0f
        const val VOLUME_CHANGE_PER_TICK = -0.05f
        const val MILLISECONDS_BETWEEN_TICKS = 100L
    }
}
