package me.avo.spottit.config

import me.avo.spottit.config.kodein
import org.junit.jupiter.api.Test

internal class DependencyTest {

    @Test fun `dependencies parse`() {

        kodein.container.bindings.forEach {
            println(it)
        }

    }

}