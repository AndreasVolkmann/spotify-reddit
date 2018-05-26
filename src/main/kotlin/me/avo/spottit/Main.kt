package me.avo.spottit

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.DynamicPlaylistController
import me.avo.spottit.controller.ManualAuthController

fun main(args: Array<String>) {
    val arguments = Arguments(args)
    if (arguments.help) return

    if (arguments.manualAuth) {
        val manualAuthController: ManualAuthController = kodein.instance()
        manualAuthController.authorize()
    } else {
        val dynamicPlaylistController: DynamicPlaylistController = kodein.instance()
        dynamicPlaylistController.updatePlaylists(arguments.configPath)
    }

}