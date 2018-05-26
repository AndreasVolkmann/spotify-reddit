package me.avo.spottit

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController
import me.avo.spottit.controller.TokenRefreshController

fun main(args: Array<String>): Unit = Arguments(args).run {
    when {
        help -> return

        manualAuth -> kodein.instance<ManualAuthController>().authorize()

        doRefresh -> kodein.instance<TokenRefreshController>().refresh()

        else -> kodein.instance<DynamicPlaylistController>().updatePlaylists(configPath)
    }
}