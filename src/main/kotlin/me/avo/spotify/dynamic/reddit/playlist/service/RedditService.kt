package me.avo.spotify.dynamic.reddit.playlist.service

import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack

interface RedditService {

    fun getTracks(): List<RedditTrack>

    fun update(amountTaken: Int)

    val isDone: Boolean

}