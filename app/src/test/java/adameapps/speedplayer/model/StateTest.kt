package adameapps.speedplayer.model

import org.junit.jupiter.api.Test

internal class StateTest {

    @Test
    fun stateInts_fromInt_createStateFromInt() {
        assert(State.fromInt(1).id == 1)
        assert(State.fromInt(2).id == 2)
        assert(State.fromInt(3).name == "NONE")
    }
}
