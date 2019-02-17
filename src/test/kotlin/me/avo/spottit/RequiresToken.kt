package me.avo.spottit

import me.avo.spottit.config.prodKodein
import me.avo.spottit.service.TokenRefreshService
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.generic.instance

interface RequiresToken {

    @BeforeAll fun beforeAll() {
        val tokenRefreshService: TokenRefreshService by prodKodein.instance()
        if (!isRefreshed) {
            tokenRefreshService.refresh()
            isRefreshed = true
        }
    }

}

private var isRefreshed = false