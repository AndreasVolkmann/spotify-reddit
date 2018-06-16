package me.avo.spottit.server

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import me.avo.spottit.util.openUrlInBrowser
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

@Disabled
internal class TemplatesTest {

    private fun render(content: FreeMarkerContent) {
        val port = 5001
        val url = "http://localhost:$port/"
        val server = embeddedServer(CIO, port) {
            install(FreeMarker) {
                setupFreemarker()
            }
            install(Routing) {
                get {
                    call.respond(content)
                }
            }
        }.start(false)
        openUrlInBrowser(url)
        Thread.sleep(3000)
        server.stop(2L, 2L, TimeUnit.SECONDS)
    }

    @Test fun auth() {
        val accessToken =
            "AQAmI_fU50XDxtJVSeRtFPK05lK8fXqVEMF7-YlUqe8QoxSTOEi6HkSsWwSheHJaUEstcKnAYAnKZfwhrCCl9PKR73AVU4PFE6xsHD2g5MrgJuZhcVQMB-v-lza-jm2GUqahrDrxoZJ3IZj9rOIOuy1t64YUjxW4lAIiGppghmc5lXV4la0aNahwg62EQhrNoPdJ1Sk0mABYzk4B-LYqDX_rEzULnHZ3Ttqso-K-6VUuuGZhelXXl5XjaBJO6qzrONXaENvdIqxH2GXwpXT8tBZ-ZvA1O3d67hohP10nomU8qvzT"
        val refreshToken =
            "AQCbUZX9XRExBTerHlXZhZs0ia84aFdq6Nje8EV9OGR3Btq00_5RBej11B9f453XtGWvBKzvoH3ZV_K8yr3dnuyfWYUDFSseMNvMmj4Zjc05ejBLMMZuO2sss3uBWqh1ZTOEVcevLfusknieV_QAsT680Ug0XlEDjZV-5RSv1Tm7I7EgpzvZDUnm7kPVeSQH4_EOzl1TsVBbXFUG902AGicJ6UBNi8nqnVMwEzQQGoViQQciI8wC5innHoYZpVx4XrRVKVfNQtVX"
        render(Templates.auth(accessToken, refreshToken))
    }

}