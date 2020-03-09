package me.avo.spottit.service.reddit

import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.RetrySupport
import me.avo.spottit.util.SubmissionParser
import me.avo.spottit.util.retry
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.SubredditReference
import org.slf4j.Logger
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
                stop("Page empty, or max page ($maxPage)")
                break@loop
            }

            val redditTracks = page.let(::filterMinimumUpvotes)

            if (redditTracks.isEmpty()) {
                stop("RedditTracks empty") // if there are no submissions left after upvote filtering, stop looking
                break@loop
            }

            redditTracks
                .asSequence()
                .filter { SubmissionParser.isValidTrackTitle(it.title) } // artist - track delimiter
                .filterNot { it.isSelfPost }
                .filterNot { it.linkFlairText in flairsToExclude }
                .map(::parse)
                .filter { track -> SubmissionParser.filterTags(track, playlist.tagFilter) }
                .filterNot { SubmissionParser.isSpotifyAlbum(URL(it.url)) }
                .toList() // filter out albums
                .let(validPosts::addAll)
        }

        if (paginator.pageNumber >= maxPage || currentSize >= playlist.maxSize) {
            stop("Max page ($maxPage) or max size (${playlist.maxSize})")
        }

        return validPosts
    }

    override fun update(amountTaken: Int) {
        logger.info("Adding $amountTaken tracks")
        currentSize += amountTaken
    }

    private lateinit var paginator: Paginator<Submission>

    private var currentSize = 0

    private fun stop(reason: String) {
        logger.info("Reddit pagination done. Reason: $reason")
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

    private fun parse(submission: Submission): RedditTrack = SubmissionParser.parse(
        submission.title, submission.linkFlairText, submission.url, submission.created
    )

    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
