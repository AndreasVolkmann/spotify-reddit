package me.avo.spottit

import me.avo.spottit.config.prodKodein
import me.avo.spottit.controller.TokenRefreshController
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.generic.instance

interface RequiresToken {

    @BeforeAll fun beforeAll() {
        val tokenRefreshController: TokenRefreshController by prodKodein.instance()
        if (!isRefreshed) {
            tokenRefreshController.refresh()
            isRefreshed = true
        }
    }

}

private var isRefreshed = false