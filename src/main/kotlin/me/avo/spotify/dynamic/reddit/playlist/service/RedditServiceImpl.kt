package me.avo.spotify.dynamic.reddit.playlist.service

import me.avo.spotify.dynamic.reddit.playlist.model.Playlist
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack
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
import java.util.*

class RedditServiceImpl(
    clientId: String,
    clientSecret: String,
    deviceName: String
) : RedditService {

    private val userAgent = UserAgent("bot", "me.avo.spotify.dynamic.reddit.playlist", "v0.1", "idajuul")
    private val credentials = Credentials.userless(clientId, clientSecret, UUID.fromString(deviceName))
    private val adapter = OkHttpNetworkAdapter(userAgent)
    private val reddit = OAuthHelper.automatic(adapter, credentials)

    private lateinit var paginator: Paginator<Submission>

    private var currentSize = 0

    override var isDone = false
        private set

    override fun getTracks(playlist: Playlist): List<RedditTrack> {

        val validPosts = mutableListOf<Submission>()

        initializePaginator(playlist)

        while (currentSize + validPosts.size < playlist.maxSize) {
            val page = paginator.next()
            logger.info("Reading page ${paginator.pageNumber}")
            if (page.isEmpty() || paginator.pageNumber >= maxPage) {
                stop()
                break
            }
            page
                .filterNot { it.isSelfPost }
                .filterNot { it.linkFlairText in flairsToExclude }
                .mapTo(validPosts) { it }
        }

        if (paginator.pageNumber >= maxPage || currentSize >= playlist.maxSize) {
            stop()
        }

        return validPosts.map {
            val fixedTitle = it.title.fixChars()
            val extraInformation = fixedTitle.getExtraInformation()
            val mix = fixedTitle.getMix()
            val fullTitle = (extraInformation + mix).fold(fixedTitle) { acc, s -> acc.replace(s, "").trim() }
            RedditTrack(
                artist = fullTitle.substringBefore("-").trim(),
                title = fullTitle.substringAfter("-").trim(),
                mix = mix.firstOrNull()?.removePrefixSuffix(),
                extraInformation = (extraInformation + mix.drop(1)).map { it.removePrefixSuffix() },
                flair = it.linkFlairText
            )
        }
    }

    private fun stop() {
        logger.info("Reddit pagination done")
        isDone = true
    }

    override fun update(amountTaken: Int) {
        logger.info("Adding $amountTaken tracks")
        currentSize += amountTaken
    }

    private fun initializePaginator(playlist: Playlist) {
        if (!::paginator.isInitialized) {
            val subreddit = reddit.subreddit(playlist.subreddit)
            paginator = subreddit.getTracks(playlist.sort, playlist.timePeriod, playlist.maxSize)
        }
    }

    private fun SubredditReference.getTracks(sort: SubredditSort, timePeriod: TimePeriod, limit: Int) = posts()
        .sorting(sort)
        .timePeriod(timePeriod)
        .build()

    private val maxPage = 20

    private val flairsToExclude = listOf("Mix", "Liveset", "Radio Show", "Album", "Upcoming AMA", "Concluded", "RIP")

    private fun String.fixChars() = replace("&amp;", "&")

    private fun String.getMix() = getEnclosedText("(", ")")

    private fun String.getExtraInformation(): List<String> = getEnclosedText("[", "]")

    private fun String.getEnclosedText(start: String, end: String) = Regex("\\$start.*?\\$end")
        .findAll(this)
        .map(MatchResult::value)
        .toList()

    private val prefixChars = listOf("(", "[")
    private val suffixChars = listOf(")", "]")

    private fun String.removePrefixSuffix() = prefixChars.fold(this) { acc, s -> acc.removePrefix(s) }
        .let { suffixChars.fold(it) { acc, s -> acc.removeSuffix(s) } }

    private val logger = LoggerFactory.getLogger(this::class.java)

}