package me.avo.spottit.service

import me.avo.spottit.config.prodKodein
import me.avo.spottit.getTestConfig
import me.avo.spottit.service.DynamicPlaylistService
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance

internal class DynamicPlaylistServiceTest {

    private val dynamicPlaylistService: DynamicPlaylistService by prodKodein.instance()

    @Test fun update() {
        dynamicPlaylistService.updatePlaylists(getTestConfig())
    }

}