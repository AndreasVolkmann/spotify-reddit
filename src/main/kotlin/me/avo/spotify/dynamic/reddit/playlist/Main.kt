package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.instance
import me.avo.spotify.dynamic.reddit.playlist.controller.DynamicPlaylistController

fun main(args: Array<String>) {
    val dynamicPlaylistController: DynamicPlaylistController = kodein.instance()
    dynamicPlaylistController.updateDynamicPlaylist()
}