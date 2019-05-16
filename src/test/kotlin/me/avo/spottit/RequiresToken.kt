package me.avo.spottit

import me.avo.spottit.service.TokenRefreshService
import org.junit.jupiter.api.BeforeAll
import org.kodein.di.generic.instance

interface RequiresToken : TestKodeinAware {

    @BeforeAll fun beforeAll() {
        val tokenRefreshService: TokenRefreshService by instance()
        if (!isRefreshed) {
            tokenRefreshService.refresh()
            isRefreshed = true
        }
    }

}

private var isRefreshed = false
