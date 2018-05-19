package me.avo.spotify.dynamic.reddit.playlist.util

import java.awt.Desktop
import java.net.URI

fun openUrlInBrowser(url: String) = Desktop.getDesktop().browse(URI(url))