package me.avo.spottit

import com.apurebase.arkenv.parse
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein
import me.avo.spottit.service.spotify.SpotifyAuthService
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

interface TestKodeinAware : KodeinAware {

    override val kodein: Kodein get() = prodKodein

    @BeforeAll fun parse() {
        if (!isParsed) {
            Arguments.parse(arrayOf("-c", ""))
        }
        val spotifyAuthService: SpotifyAuthService by instance()
        if (!isRefreshed) {
            spotifyAuthService.refresh()
            isRefreshed = true
        }
    }

}

private var isParsed = false
private var isRefreshed = false
