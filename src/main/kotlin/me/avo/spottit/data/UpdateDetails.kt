package me.avo.spottit.data

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist

data class UpdateDetails(
    val configuration: Configuration,
    val playlist: Playlist
)
