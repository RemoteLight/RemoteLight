package io.github.remotelight.api

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.remotelight.core.RemoteLightCore
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object RemoteLightApi {

    private lateinit var server: ApplicationEngine

    fun start() {
        server = embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    call.respondText("Hello, world!")
                }
            }
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server.stop(3000L, 2000L)
    }

}

fun main() {
    RemoteLightCore()
    RemoteLightApi.start()
}
