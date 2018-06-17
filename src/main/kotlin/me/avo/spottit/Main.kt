package me.avo.spottit

import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.util.YamlConfigReader
import org.kodein.di.generic.instance
import java.io.File

fun main(args: Array<String>): Unit = Arguments(args).run {
    if (help) return
    val configuration = YamlConfigReader.read(File(configPath).readText())
    val manualAuthController: ManualAuthController by prodKodein.instance()
    val tokenRefreshController: TokenRefreshController by prodKodein.instance()
    val dynamicPlaylistController: DynamicPlaylistController by prodKodein.instance()

    when {
        manualAuth -> manualAuthController.authorize(configuration)

        doRefresh -> tokenRefreshController.refresh()

        else -> dynamicPlaylistController.updatePlaylists(configuration)
    }
}