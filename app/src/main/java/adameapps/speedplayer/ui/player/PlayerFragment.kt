package adameapps.speedplayer.ui.player

import adameapps.speedplayer.R
import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.gps.SpeedChangeListener
import adameapps.speedplayer.player.MusicPlaybackListener
import adameapps.speedplayer.ui.PlayerService
import android.content.Intent
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
        playButton.text = if(isPlaying()) {
            "STOP"
        } else {
            "START"
        }
        playButton.setOnClickListener {
            onPlayButtonPressed()
        }
        PlayerService.currentTrackTitle?.let {
            playerViewModel.showTrackTitle(it)
        }
        return root
    }

    override fun onMusicPlaybackFailure() {
        TODO("Not yet implemented")
    }

    override fun onMusicTitleSwitch(title: String) {
        playerViewModel.showTrackTitle(title)
    }

    override fun onSpeedChange(speedInKilometersPerHour: Int) {
        if (isPlaying()) {
            PlayerService.reportSpeed(speedInKilometersPerHour)
            playerViewModel.onSpeedChange(isPlaying(), speedInKilometersPerHour)
        }
    }

    override fun onGpsEnable() {
        playerViewModel.onGpsEnable()
    }

    override fun onGpsDisable() {
        playerViewModel.onGpsDisable()
    }

    private fun onPlayButtonPressed() {
        if (!isPlaying()) {
            if (DataManager.readAnyTrackSelectedForEachState(requireActivity())) {
                PlayerService.init(requireActivity(), this, this)
                requireActivity().startService(Intent(requireContext(), PlayerService::class.java))
                playButton.text = "STOP"
                playerViewModel.showPlayerTurnedOff()
            } else {
                //TODO: inform about the problem - no tracks selected
            }
        } else {
            PlayerService.stop()
            requireActivity().stopService(Intent(requireContext(), PlayerService::class.java))
            playButton.text = "START"
        }
    }

    private fun isPlaying(): Boolean {
        return PlayerService.isPlaying()
    }
}
