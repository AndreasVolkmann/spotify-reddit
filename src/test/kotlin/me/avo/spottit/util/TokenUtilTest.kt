package me.avo.spottit.util

import me.avo.spottit.config.Arguments
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.junit.jupiter.api.Test

internal class TokenUtilTest {

    @Test fun `refresh token should exist`() {
        Arguments.refreshToken.shouldNotBeNullOrBlank()
    }

}