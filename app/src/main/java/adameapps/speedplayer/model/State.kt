package adameapps.speedplayer.model

enum class State(val id: Int) {
    NONE(3), LOW(0), MEDIUM(1), HIGH(2);

    companion object {
        fun fromInt(id: Int) = values().first { it.id == id }
    }
}
