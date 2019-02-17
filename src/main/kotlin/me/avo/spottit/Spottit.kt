package me.avo.spottit

import me.avo.spottit.config.Arguments
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.util.YamlConfigReader
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File

class Spottit(private val arguments: Arguments, kodein: Kodein) : KodeinAware by kodein {

    private val configuration by lazy { YamlConfigReader.read(File(arguments.configPath).readText()) }
    private val manualAuthController: ManualAuthController by instance()
    private val tokenRefreshController: TokenRefreshController by instance()
    private val dynamicPlaylistController: DynamicPlaylistController by instance()

    fun run() = with(arguments) {
        when {
            help -> return@with

            manualAuth -> manualAuthController.authorize(configuration)

            doRefresh -> tokenRefreshController.refresh()

            else -> dynamicPlaylistController.updatePlaylists(configuration)
        }
    }

}