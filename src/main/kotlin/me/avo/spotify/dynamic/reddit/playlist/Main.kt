package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.instance
import me.avo.spotify.dynamic.reddit.playlist.config.Arguments
import me.avo.spotify.dynamic.reddit.playlist.config.kodein
import me.avo.spotify.dynamic.reddit.playlist.controller.DynamicPlaylistController

fun main(args: Array<String>) {
    val arguments = Arguments(args)
    if (arguments.help) return

    val dynamicPlaylistController: DynamicPlaylistController = kodein.instance()
    dynamicPlaylistController.updatePlaylists(arguments.configPath)
}