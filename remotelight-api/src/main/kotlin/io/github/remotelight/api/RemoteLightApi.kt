package io.github.remotelight.api

import com.fasterxml.jackson.databind.SerializationFeature
import io.github.remotelight.api.routes.registerOutputsRoutes
import io.github.remotelight.api.routes.registerRegistriesRoutes
import io.github.remotelight.api.routes.registerScenesRoutes
import io.github.remotelight.controller.RemoteLightController
import io.github.remotelight.core.RemoteLightCore
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

object RemoteLightApi {

    private lateinit var server: ApplicationEngine

    fun start() {
        server = embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
            install(CORS) {
                anyHost()
                method(HttpMethod.Options)
                method(HttpMethod.Put)
                method(HttpMethod.Patch)
                method(HttpMethod.Delete)
                allowNonSimpleContentTypes = true
            }
            install(StatusPages) {
                exception<Throwable> { cause ->
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause))
                }
            }

            registerOutputsRoutes()
            registerScenesRoutes()
            registerRegistriesRoutes()
        }.start(wait = false)
    }

    fun stop() {
        server.stop(3000L, 2000L)
    }

}

fun main() {
    RemoteLightCore()
    RemoteLightController
    RemoteLightApi.start()
}
