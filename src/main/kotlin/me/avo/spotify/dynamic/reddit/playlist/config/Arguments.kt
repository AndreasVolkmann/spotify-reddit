package me.avo.spotify.dynamic.reddit.playlist.config

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter

class Arguments(args: Array<String>) {

    @Parameter(names = ["-c", "--config"], required = true, description = "The path to your config.yml")
    lateinit var configPath: String
        private set

    @Parameter(names = ["--help", "-h"], help = true)
    var help = false
        private set

    private fun parse(args: Array<String>) = JCommander
        .newBuilder()
        .addObject(this)
        .build()
        .let {
            it.parse(*args)
            if (help) {
                it.usage()
            }
        }

    init {
        parse(args)
    }

}