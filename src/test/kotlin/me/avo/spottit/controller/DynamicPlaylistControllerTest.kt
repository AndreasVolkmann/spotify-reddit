package me.avo.spottit.controller

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.kodein
import me.avo.spottit.getTestConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DynamicPlaylistControllerTest {

    @Test fun update() {

        kodein.instance<DynamicPlaylistController>().updatePlaylists(getTestConfig())

    }

}