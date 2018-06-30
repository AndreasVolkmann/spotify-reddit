package me.avo.spottit.config

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class ArgumentsTest {

    @Test fun `parse args correctly`() {
        val configPath = "config.yml"
        val args = Arkuments(arrayOf("-c", configPath, "-ma", "-r", "-h"))
        with(args) {
            this.configPath shouldBeEqualTo configPath
            manualAuth shouldBe true
            doRefresh shouldBe true
            help shouldBe true
        }
    }

}