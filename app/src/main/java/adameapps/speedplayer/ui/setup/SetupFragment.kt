package adameapps.speedplayer.ui.setup

import adameapps.speedplayer.MAXIMUM_HIGH_THRESHOLD
import adameapps.speedplayer.MINIMUM_MEDIUM_THRESHOLD
import adameapps.speedplayer.R
import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.model.MusicLibrary
import adameapps.speedplayer.model.State
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SetupFragment : Fragment() {

    private val setupViewModel: SetupViewModel by viewModels()

    private var currentlyChecked = 0
    private var currentMediumSpeedThreshold: Int = 0
    private var currentHighSpeedThreshold: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_setup, container, false)

        currentMediumSpeedThreshold = DataManager.readMediumThreshold(requireActivity())
        currentHighSpeedThreshold = DataManager.readHighThreshold(requireActivity())

        val mediumSpeedThresholdText: TextView =
            root.findViewById(R.id.text_medium_speed_threshold)
        setupViewModel.mediumThresholdInfo.observe(viewLifecycleOwner, {
            mediumSpeedThresholdText.text = it
        })
        val highSpeedThresholdText: TextView =
            root.findViewById(R.id.text_high_speed_threshold)
        setupViewModel.highThresholdInfo.observe(viewLifecycleOwner, {
            highSpeedThresholdText.text = it
        })
        val mediumSpeedThresholdSeekBar: SeekBar =
            root.findViewById(R.id.seekbar_medium_speed_threshold)
        val highSpeedThresholdSeekBar: SeekBar =
            root.findViewById(R.id.seekbar_high_speed_threshold)
        mediumSpeedThresholdSeekBar.min = MINIMUM_MEDIUM_THRESHOLD
        mediumSpeedThresholdSeekBar.max = currentHighSpeedThreshold
        mediumSpeedThresholdSeekBar.progress = DataManager.readMediumThreshold(requireActivity())
        mediumSpeedThresholdSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentMediumSpeedThreshold = progress
                setupViewModel.updateMediumThreshold(progress)
                highSpeedThresholdSeekBar.min = currentMediumSpeedThreshold
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                DataManager.saveMediumThreshold(requireActivity(), currentMediumSpeedThreshold)
            }
        })
        highSpeedThresholdSeekBar.min = currentMediumSpeedThreshold - 1
        highSpeedThresholdSeekBar.max = MAXIMUM_HIGH_THRESHOLD
        highSpeedThresholdSeekBar.progress = DataManager.readHighThreshold(requireActivity())
        highSpeedThresholdSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentHighSpeedThreshold = progress
                setupViewModel.updateHighThreshold(progress)
                mediumSpeedThresholdSeekBar.max = currentHighSpeedThreshold - 1
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                DataManager.saveHighThreshold(requireActivity(), currentHighSpeedThreshold)
            }
        })
        setupViewModel.updateMediumThreshold(currentMediumSpeedThreshold)
        setupViewModel.updateHighThreshold(currentHighSpeedThreshold)

        val radioGroup: RadioGroup = root.findViewById(R.id.radiogroup_setup)
        radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radiobutton_setup_low -> currentlyChecked = State.LOW.id
                R.id.radiobutton_setup_medium -> currentlyChecked = State.MEDIUM.id
                R.id.radiobutton_setup_high -> currentlyChecked = State.HIGH.id
                R.id.radiobutton_setup_clear -> currentlyChecked = State.NONE.id
            }
        }
        val lowRadioButton: RadioButton = root.findViewById(R.id.radiobutton_setup_low)
        lowRadioButton.isChecked = true

        val viewManager = LinearLayoutManager(requireContext())
        val recyclerViewSetupTracks: RecyclerView =
            root.findViewById(R.id.recyclerview_setup_tracks)
        recyclerViewSetupTracks.apply {
            layoutManager = viewManager
            adapter = MusicListAdapter()
        }

        return root
    }

    inner class MusicListAdapter :
        RecyclerView.Adapter<MusicListAdapter.MyViewHolder>() {
        inner class MyViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            val layout = LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.recyclerview_setup_track_list, parent, false
                ) as LinearLayout
            return MyViewHolder(layout)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val musicFile = MusicLibrary.musicList[position]
            val trackTitle: TextView =
                holder.itemView.findViewById(R.id.recyclerview_setup_text_track_title)
            trackTitle.text = musicFile.title
            val trackPlaylist: TextView =
                holder.itemView.findViewById(R.id.recyclerview_setup_text_track_playlist)
            trackPlaylist.text = DataManager.readTrackType(requireActivity(), musicFile).toString()
            val trackOption: LinearLayout =
                holder.itemView.findViewById(R.id.recyclerview_setup_linearlayout_track_list)
            trackOption.setOnClickListener {
                DataManager.saveTrackType(
                    requireActivity(), musicFile, State.fromInt(currentlyChecked)
                )
                trackPlaylist.text = State.fromInt(currentlyChecked).toString()
            }
        }

        override fun getItemCount() = MusicLibrary.musicList.size
    }
}
