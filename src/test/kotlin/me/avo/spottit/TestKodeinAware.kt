package me.avo.spottit

import com.apurebase.arkenv.parse
import me.avo.spottit.config.Arguments
import me.avo.spottit.config.prodKodein
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

interface TestKodeinAware : KodeinAware {

    override val kodein: Kodein get() = prodKodein

    @BeforeAll fun parse() {
        if (!isParsed) {
            Arguments.parse(arrayOf("-c", ""))
        }
    }

}

private var isParsed = false
