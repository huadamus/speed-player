package adameapps.speedplayer.player

interface MusicPlaybackListener {
    fun onMusicPlaybackFailure()
    fun onMusicTitleSwitch(title: String)
}
