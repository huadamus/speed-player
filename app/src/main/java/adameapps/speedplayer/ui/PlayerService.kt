package adameapps.speedplayer.ui

import adameapps.speedplayer.R
import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.gps.GPSManager
import adameapps.speedplayer.gps.SpeedChangeListener
import adameapps.speedplayer.player.MusicManager
import adameapps.speedplayer.player.MusicPlaybackListener
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PlayerService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        hideNotification(this)
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_LOW_IMPORTANCE,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_LOW_IMPORTANCE)
            .setContentText("")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    0
                )
            )
        updateNotification(this, "")
    }

    companion object {
        private const val NOTIFICATION_ID = 8001
        private const val CHANNEL_LOW_IMPORTANCE = "speed_player_low_importance"

        private lateinit var notificationBuilder: NotificationCompat.Builder
        private var musicManager: MusicManager? = null
        private lateinit var gpsManager: GPSManager
        var currentTrackTitle: String? = null

        fun init(
            activity: Activity,
            musicPlaybackListener: MusicPlaybackListener,
            speedChangeListener: SpeedChangeListener
        ) {
            musicManager = MusicManager(
                activity,
                musicPlaybackListener,
                DataManager.readMediumThreshold(activity),
                DataManager.readHighThreshold(activity)
            )
            updateNotification(activity, "")
            gpsManager = GPSManager(activity, speedChangeListener)
            gpsManager.init()
        }

        fun assignMusicPlaybackListener(musicPlaybackListener: MusicPlaybackListener) {
            musicManager?.assignMusicPlaybackListener(musicPlaybackListener)
        }

        fun start() {
            musicManager?.start()
        }

        fun reportSpeed(speedInKilometersPerHour: Int) {
            musicManager?.reportSpeed(speedInKilometersPerHour)
        }

        fun stop() {
            musicManager?.stop()
            currentTrackTitle = null
        }

        fun isPlaying(): Boolean {
            return if (musicManager == null) {
                false
            } else {
                musicManager!!.isPlaying()
            }
        }

        fun updateMediumSpeedThreshold(mediumMusicThresholdSpeed: Int) {
            musicManager?.let {
                it.mediumMusicThresholdSpeed = mediumMusicThresholdSpeed
            }
        }

        fun updateHighSpeedThreshold(highMusicThresholdSpeed: Int) {
            musicManager?.let {
                it.highMusicThresholdSpeed = highMusicThresholdSpeed
            }
        }

        fun updateNotification(context: Context, title: String) {
            if (this::notificationBuilder.isInitialized) {
                currentTrackTitle = title
                notificationBuilder.setContentText(title)
                with(NotificationManagerCompat.from(context)) {
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }
        }

        private fun hideNotification(context: Context) {
            with(NotificationManagerCompat.from(context)) {
                cancel(NOTIFICATION_ID)
            }
        }
    }
}
