package me.avo.spottit.config

import com.apurebase.arkenv.parse
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

internal class ArgumentsTest {

    @Test fun `parse args correctly`() {
        val configPath = "config.yml"
        val args = Arguments.parse(arrayOf("-c", configPath, "-ma", "-r"))
        expectThat(args) {
            get { this.configPath }.isEqualTo(configPath)
            get { manualAuth }.isTrue()
            get { doRefresh }.isTrue()
            get { help }.isFalse()
            get { editDistance }.isEqualTo(15)
        }
    }
}
