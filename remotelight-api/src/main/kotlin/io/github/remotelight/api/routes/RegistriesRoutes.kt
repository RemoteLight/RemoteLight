package io.github.remotelight.api.routes

import io.github.remotelight.core.effect.EffectRegistry
import io.github.remotelight.core.output.OutputRegistry
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.registriesRouting() {
    val outputRegistry by inject<OutputRegistry>()
    val effectRegistry by inject<EffectRegistry>()

    route("/registries") {
        get("/outputs") {
            val outputTypes = outputRegistry.getRegisteredOutputTypes()
            call.respond(HttpStatusCode.OK, outputTypes)
        }
        get("/effects") {
            val effectTypes = effectRegistry.getRegisteredEffects()
            call.respond(HttpStatusCode.OK, effectTypes)
        }
    }
}

fun Application.registerRegistriesRoutes() {
    routing {
        registriesRouting()
    }
}
