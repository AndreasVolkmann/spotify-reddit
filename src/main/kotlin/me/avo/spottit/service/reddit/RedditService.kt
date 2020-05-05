package me.avo.spottit.service.reddit

import me.avo.spottit.model.RedditTrack

interface RedditService {

    fun getRedditTracks(): List<RedditTrack>

    /**
     * Sets the current size of the collection of already found tracks.
     * The reddit service will stop processing when the limit is reached.
     */
    fun setCurrentSize(size: Int)

    fun isDone(): Boolean

}
