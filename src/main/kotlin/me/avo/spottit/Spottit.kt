package me.avo.spottit

import me.avo.spottit.config.Arguments
import me.avo.spottit.service.DynamicPlaylistService
import me.avo.spottit.service.spotify.ManualAuthService
import me.avo.spottit.service.spotify.TokenRefreshService
import me.avo.spottit.util.YamlConfigReader
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File

class Spottit(kodein: Kodein) : KodeinAware by kodein {

    private val configuration by lazy { YamlConfigReader.read(File(Arguments.configPath).readText()) }
    private val manualAuthService: ManualAuthService by instance()
    private val tokenRefreshService: TokenRefreshService by instance()
    private val dynamicPlaylistService: DynamicPlaylistService by instance()

    fun run() = with(Arguments) {
        when {
            help -> return@with

            manualAuth -> manualAuthService.authorize(configuration)

            doRefresh -> tokenRefreshService.refresh()

            else -> dynamicPlaylistService.updatePlaylists(configuration)
        }
    }
}
