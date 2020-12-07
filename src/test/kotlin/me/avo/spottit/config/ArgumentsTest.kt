package me.avo.spottit.config

import com.apurebase.arkenv.parse
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

internal class ArgumentsTest {
    private val configPath = "config.yml"

    @Test fun `parse args correctly`() {
        val args = Arguments.parse(arrayOf("-c", configPath, "-ma", "-r"))
        expectThat(args) {
            get { this.configPath } isEqualTo configPath
            get { manualAuth }.isTrue()
            get { doRefresh }.isTrue()
            get { help }.isFalse()
            get { editDistance } isEqualTo 15
        }
    }

    @Test fun `manual auth should not require refresh token`() {
        Arguments.parse(arrayOf("-c", configPath, "-ma", "--refreshTokenFile", "does_not_exist"))
    }
}
