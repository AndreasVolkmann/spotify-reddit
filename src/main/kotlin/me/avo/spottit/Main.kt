package me.avo.spottit

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.AutomaticAuthController
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.util.YamlConfigReader
import java.io.File

fun main(args: Array<String>): Unit = Arguments(args).run {
    if (help) return
    val configuration = YamlConfigReader.read(File(configPath).readText())
    when {
        manualAuth -> kodein.instance<ManualAuthController>().authorize(configuration)

        automaticAuth -> kodein.instance<AutomaticAuthController>().authorize(configuration)

        doRefresh -> kodein.instance<TokenRefreshController>().refresh()

        else -> kodein.instance<DynamicPlaylistController>().updatePlaylists(configuration)
    }
}