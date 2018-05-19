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
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.references.SubredditReference
import java.util.*

class RedditServiceImpl(
    private val clientId: String,
    private val clientSecret: String,
    private val deviceName: String
) : RedditService {

    val userAgent = UserAgent("bot", "me.avo.spotify.dynamic.reddit.playlist", "v0.1", "idajuul")
    val credentials = Credentials.userless(clientId, clientSecret, UUID.fromString(deviceName))
    val adapter = OkHttpNetworkAdapter(userAgent)
    val reddit = OAuthHelper.automatic(adapter, credentials)

    override fun getTracks(playlist: Playlist): List<RedditTrack> {
        val subreddit = reddit.subreddit(playlist.subreddit)

        val validPosts = mutableListOf<Submission>()

        val posts = subreddit.getTracks(playlist.sort, playlist.timePeriod, limit)

        while (validPosts.size < max) {
            val page = posts.next()
            if (page.isEmpty()) break
            page
                .filterNot { it.isSelfPost }
                .filterNot { it.linkFlairText in flairsToExclude }
                //.filter { it.linkFlairText == null }
                .mapTo(validPosts) { it }
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

    private fun SubredditReference.getTopPosts(): DefaultPaginator<Submission> =
        getTracks(SubredditSort.TOP, TimePeriod.ALL, limit)

    private fun SubredditReference.getLatestPosts() = getTracks(SubredditSort.TOP, TimePeriod.WEEK, limit)

    private fun SubredditReference.getTracks(sort: SubredditSort, timePeriod: TimePeriod, limit: Int) = posts()
        .sorting(sort)
        .limit(limit)
        .timePeriod(timePeriod)
        .build()

    private val max = 20
    private val limit = 50

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

}