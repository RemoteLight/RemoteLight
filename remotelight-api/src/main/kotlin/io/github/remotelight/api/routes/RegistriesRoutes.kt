package io.github.remotelight.api.routes

import io.github.remotelight.core.effect.EffectRegistry
import io.github.remotelight.core.output.OutputRegistry
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.registriesRouting() {
    val outputRegistry by inject<OutputRegistry>()
    val effectRegistry by inject<EffectRegistry>()

    route("/registries") {
        get("/outputs") {
            val outputTypes = outputRegistry.getRegisteredOutputTypes()
            call.respond(outputTypes)
        }

        get("/effects") {
            val effectTypes = effectRegistry.getRegisteredEffects()
            call.respond(effectTypes)
        }
    }
}

fun Application.registerRegistriesRoutes() {
    routing {
        registriesRouting()
    }
}
