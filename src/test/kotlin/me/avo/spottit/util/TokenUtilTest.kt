package me.avo.spottit.util

import me.avo.spottit.config.Arguments
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNullOrBlank

internal class TokenUtilTest {

    @Test fun `refresh token should exist`() {
        expectThat(Arguments.refreshToken).not().isNullOrBlank()
    }

}