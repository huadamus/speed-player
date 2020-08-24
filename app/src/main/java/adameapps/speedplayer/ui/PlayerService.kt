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
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        hideNotification()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_LOW_IMPORTANCE,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        val activityIntent = Intent(this, MainActivity::class.java)
        val showAppIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        notificationBuilder.setContentTitle("NOTIFICATION")
            .setContentText("TEST")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    "BUTTON",
                    showAppIntent
                ).build()
            )
        showNotification(this)
        shownNotification = true
    }

    private fun hideNotification() {
        with(NotificationManagerCompat.from(this)) {
            cancel(NOTIFICATION_ID)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 8001
        const val CHANNEL_LOW_IMPORTANCE = "speed_player_low_importance"

        private lateinit var notificationBuilder: NotificationCompat.Builder
        private var shownNotification = false
        var musicManager: MusicManager? = null
        private lateinit var gpsManager: GPSManager

        fun init(
            activity: Activity,
            musicPlaybackListener: MusicPlaybackListener,
            speedChangeListener: SpeedChangeListener
        ) {
            shownNotification = false
            notificationBuilder = NotificationCompat.Builder(activity, CHANNEL_LOW_IMPORTANCE)
            musicManager = MusicManager(
                activity,
                musicPlaybackListener,
                DataManager.readMediumThreshold(activity),
                DataManager.readHighThreshold(activity)
            )
            gpsManager = GPSManager(activity, speedChangeListener)
            gpsManager.init()
        }

        fun start() {
            musicManager?.start()
        }

        fun reportSpeed(speedInKilometersPerHour: Int) {
            musicManager?.reportSpeed(speedInKilometersPerHour)
        }

        fun stop() {
            musicManager?.stop()
            musicManager?.terminate()
        }

        fun isPlaying(): Boolean {
            return if(musicManager == null) {
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
            notificationBuilder.setContentText(title)
            showNotification(context)
        }

        private fun showNotification(context: Context) {
            if(shownNotification) {
                with(NotificationManagerCompat.from(context)) {
                    notify(NOTIFICATION_ID, notificationBuilder.build())
                }
            }
        }
    }
}
