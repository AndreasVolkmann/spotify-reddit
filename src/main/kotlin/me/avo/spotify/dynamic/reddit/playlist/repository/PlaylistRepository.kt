package me.avo.spotify.dynamic.reddit.playlist.repository

import me.avo.spotify.dynamic.reddit.playlist.model.Playlist

interface PlaylistRepository {

    fun getPlaylists(): Collection<Playlist>

}