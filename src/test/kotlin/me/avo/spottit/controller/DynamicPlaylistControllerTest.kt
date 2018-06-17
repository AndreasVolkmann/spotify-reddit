package me.avo.spottit.controller

import me.avo.spottit.config.prodKodein
import me.avo.spottit.getTestConfig
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance

internal class DynamicPlaylistControllerTest {

    private val dynamicPlaylistController: DynamicPlaylistController by prodKodein.instance()

    @Test fun update() {
        dynamicPlaylistController.updatePlaylists(getTestConfig())
    }

}