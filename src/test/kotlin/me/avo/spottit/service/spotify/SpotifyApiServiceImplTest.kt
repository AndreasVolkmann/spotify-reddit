package me.avo.spottit.service.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.NotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNull

internal class SpotifyApiServiceImplTest {

    @Test fun `getTrack - NotFoundException should return null`() {
        val spotifyApi = mockk<SpotifyApi> {
            every { getTrack("test") }.throws(NotFoundException())
        }
        val spotifyApiService: SpotifyApiService = SpotifyApiServiceImpl(spotifyApi)

        val result = spotifyApiService.getTrack("test")

        expectThat(result).isNull()
    }
}