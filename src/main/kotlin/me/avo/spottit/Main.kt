package me.avo.spottit

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.DynamicPlaylistController

fun main(args: Array<String>) {
    val arguments = Arguments(args)
    if (arguments.help) return

    val dynamicPlaylistController: DynamicPlaylistController = kodein.instance()
    dynamicPlaylistController.updatePlaylists(arguments.configPath)
}