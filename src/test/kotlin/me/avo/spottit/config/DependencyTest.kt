package me.avo.spottit.config

import me.avo.spottit.TestKodeinAware
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DependencyTest : TestKodeinAware {

    @Test fun `dependencies parse`() {
        kodein.container.tree.bindings.forEach(::println)
    }

}
