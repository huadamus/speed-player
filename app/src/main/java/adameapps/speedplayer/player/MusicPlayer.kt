package adameapps.speedplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.IOException

class MusicPlayer(
    private val context: Context,
    private val musicPlaybackFailureListener: MusicPlaybackFailureListener
) {
    private val mediaPlayer = MediaPlayer()

    fun play(uri: Uri) {
        try {
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            musicPlaybackFailureListener.onMusicPlaybackFailure()
            e.printStackTrace()
        }
        mediaPlayer.start()
    }
}
