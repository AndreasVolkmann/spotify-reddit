package me.avo.spottit.exception

import me.avo.spottit.model.Playlist

class TooFewTopAllTracksException(playlist: Playlist, found: Int) : IllegalStateException(
    "Top $playlist found too few tracks. $found / ${playlist.maxSize}"
)
