package me.avo.spottit.service

import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.RetrySupport
import me.avo.spottit.util.SubmissionParser
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.SubredditReference
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class RedditServiceImpl(
    private val playlist: Playlist,
    private val flairsToExclude: List<String>,
    private val maxPage: Int,
    redditCredentials: RedditCredentials
) : RedditService, RetrySupport {

    private val reddit by lazy {
        val (clientId, clientSecret, deviceName) = redditCredentials
        val userAgent = UserAgent("bot", "me.avo.spottit", "v0.2", "idajuul")
        val adapter = OkHttpNetworkAdapter(userAgent)
        val credentials = Credentials.userless(clientId, clientSecret, UUID.fromString(deviceName))
        OAuthHelper.automatic(adapter, credentials)
    }

    override var isDone = false
        private set

    override fun getTracks(): List<RedditTrack> {
        val validPosts = mutableListOf<RedditTrack>()

        initializePaginator()

        loop@ while (currentSize + validPosts.size < playlist.maxSize) {
            val page = retry(paginator::next)
            logger.info("Reading page ${paginator.pageNumber}")

            if (page.isEmpty() || paginator.pageNumber >= maxPage) {
                stop()
                break@loop
            }

            val redditTracks = page
                .filterNot { it.isSelfPost }
                .filterNot { it.linkFlairText in flairsToExclude }
                .filter { SubmissionParser.isValidTrackTitle(it.title) } // artist - track delimiter
                .let(::filterMinimumUpvotes)
                .map(::parse)

            if (redditTracks.isEmpty()) {
                stop() // if there are no submissions left after upvote filtering, stop looking
                break@loop
            }

            redditTracks
                .filter { track -> SubmissionParser.filterTags(track, playlist.tagFilter) }
                .filterNot { SubmissionParser.isSpotifyAlbum(URL(it.url)) } // filter out albums
                .let(validPosts::addAll)
        }

        if (paginator.pageNumber >= maxPage || currentSize >= playlist.maxSize) {
            stop()
        }

        return validPosts
    }

    override fun update(amountTaken: Int) {
        logger.info("Adding $amountTaken tracks")
        currentSize += amountTaken
    }

    private lateinit var paginator: Paginator<Submission>

    private var currentSize = 0

    private fun stop() {
        logger.info("Reddit pagination done")
        isDone = true
    }

    private fun initializePaginator() {
        if (!::paginator.isInitialized) {
            val subreddit = retry { reddit.subreddit(playlist.subreddit) }
            paginator = subreddit.getTracks(playlist.sort, playlist.timePeriod)
        }
    }

    private fun SubredditReference.getTracks(sort: SubredditSort, timePeriod: TimePeriod) = posts()
        .sorting(sort)
        .timePeriod(timePeriod)
        .build()

    private fun filterMinimumUpvotes(submissions: List<Submission>): List<Submission> =
        playlist.minimumUpvotes?.let { submissions.filter { it.score > playlist.minimumUpvotes } } ?: submissions

    override val stackSize = 5

    override fun mapTimeout(ex: Exception): Int = 2000

    private fun parse(submission: Submission): RedditTrack = SubmissionParser.parse(
        submission.title, submission.linkFlairText, submission.url, submission.created
    )

    private val logger = LoggerFactory.getLogger(this::class.java)
}
