package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import java.awt.Desktop
import java.net.URI

fun openUrlInBrowser(url: String) = Desktop.getDesktop().browse(URI(url))

fun Track.format() = "${artists.map { it.name }} - $name"

fun Track.artistString() = artists.joinToString(" ") { it.name }

fun String.getEnclosedText(start: String, end: String) = Regex("\\$start.*?\\$end")
    .findAll(this)
    .map(MatchResult::value)
    .toList()