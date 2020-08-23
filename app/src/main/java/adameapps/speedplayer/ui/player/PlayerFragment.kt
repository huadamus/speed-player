package adameapps.speedplayer.ui.player

import adameapps.speedplayer.R
import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.gps.GPSManager
import adameapps.speedplayer.gps.SpeedChangeListener
import adameapps.speedplayer.player.MusicManager
import adameapps.speedplayer.player.MusicPlaybackListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class PlayerFragment : Fragment(), MusicPlaybackListener, SpeedChangeListener {

    private val playerViewModel: PlayerViewModel by viewModels()

    private lateinit var playButton: Button

    private lateinit var musicManager: MusicManager
    private lateinit var gpsManager: GPSManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_player, container, false)

        val speedMeterText: TextView = root.findViewById(R.id.text_speed_meter)
        playerViewModel.speedMeter.observe(viewLifecycleOwner, {
            speedMeterText.text = it
        })
        val trackTitleText: TextView = root.findViewById(R.id.text_title)
        playerViewModel.trackTitle.observe(viewLifecycleOwner, {
            trackTitleText.text = it
        })

        playButton = root.findViewById(R.id.button_play_pause)
        playButton.setOnClickListener {
            onPlayButtonPressed()
        }


        //to service
        musicManager = MusicManager(
            requireActivity(),
            this,
            DataManager.readMediumThreshold(requireActivity()),
            DataManager.readHighThreshold(requireActivity())
        )
        gpsManager = GPSManager(requireContext(), this)
        gpsManager.init()
        //until here


        return root
    }

    override fun onMusicPlaybackFailure() {
        TODO("Not yet implemented")
    }

    override fun onMusicTitleSwitch(title: String) {
        playerViewModel.showTrackSwitch(title)
    }

    override fun onSpeedChange(speedInKilometersPerHour: Int) {
        musicManager.reportSpeed(speedInKilometersPerHour)
        playerViewModel.onSpeedChange(musicManager.isPlaying(), speedInKilometersPerHour)
    }

    override fun onGpsEnable() {
        playerViewModel.onGpsEnable()
    }

    override fun onGpsDisable() {
        playerViewModel.onGpsDisable()
    }

    private fun onPlayButtonPressed() {
        if (playButton.text != "STOP") {
            if (DataManager.readAnyTrackSelectedForEachState(requireActivity())) {
                playButton.text = "STOP"
                playerViewModel.showPlayerTurnedOff()
                musicManager.start()
            } else {
                //TODO: inform about the problem - no tracks selected
            }
        } else {
            musicManager.stop()
            playButton.text = "START"
        }
    }
}
