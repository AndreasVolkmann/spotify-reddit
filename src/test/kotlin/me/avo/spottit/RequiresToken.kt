package me.avo.spottit

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.TokenRefreshController
import org.junit.jupiter.api.BeforeAll

interface RequiresToken {

    @BeforeAll fun beforeAll() {
        if (!isRefreshed) {
            kodein.instance<TokenRefreshController>().refresh()
            isRefreshed = true
        }
    }

}

private var isRefreshed = false