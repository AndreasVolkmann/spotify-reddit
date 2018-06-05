package me.avo.spottit.util

import org.amshove.kluent.shouldNotBeNullOrBlank
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TokenUtilTest {

    @Test fun `refresh token should exist`() {
        TokenUtil.getRefreshToken().shouldNotBeNullOrBlank()
    }

}