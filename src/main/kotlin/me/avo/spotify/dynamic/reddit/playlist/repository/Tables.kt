package me.avo.spotify.dynamic.reddit.playlist.repository

import org.jetbrains.exposed.sql.Table

val tables = arrayOf(Playlists)

object Playlists : Table("playlist") {

    val id = varchar("id", 22).primaryKey()
    val userId = varchar("userId", 20)
    val maxSize = integer("max_size")

}

