package io.github.remotelight.api.routes

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.remotelight.api.ErrorResponse
import io.github.remotelight.api.SuccessResponse
import io.github.remotelight.api.models.OutputConfigModel
import io.github.remotelight.api.models.toModel
import io.github.remotelight.api.models.toWrapper
import io.github.remotelight.core.output.OutputManager
import io.github.remotelight.core.output.config.JsonOutputConfigManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.outputsRouting() {
    val outputManager by inject<OutputManager>()
    val jsonOutputConfigManager by inject<JsonOutputConfigManager>()
    val objectMapper by inject<ObjectMapper>()

    route("/outputs") {
        get {
            val outputConfigs = outputManager.getOutputs().map {
                it.config.toModel()
            }
            call.respond(outputConfigs)
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Missing or malformed id")
            )
            val output = outputManager.getOutputById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("No output with id $id")
            )
            call.respond(output.config.toModel())
        }
        post {
            val outputConfigModel = call.receive<OutputConfigModel>()
            if (outputConfigModel.id != null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("The id must not be defined."))
                return@post
            }
            val id = OutputManager.generateOutputId()
            val outputConfig = jsonOutputConfigManager.createOutputConfig(outputConfigModel.toWrapper(id))
            val output = outputManager.createAndAddOutput(outputConfig)
            call.respond(HttpStatusCode.Created, output.config.toModel())
        }
        patch("{id}") {
            val id = call.parameters["id"] ?: return@patch call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Missing or malformed id")
            )
            val outputConfigModel = call.receive<OutputConfigModel>()
            if (outputConfigModel.id != null && outputConfigModel.id != id) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("The id cannot be changed."))
                return@patch
            }
            val existingOutputConfig = outputManager.getOutputById(id)?.config ?: return@patch call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("No output with id $id")
            )
            if (outputConfigModel.identifier != existingOutputConfig.outputIdentifier) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("The output identifier cannot be changed."))
                return@patch
            }
            val properties = outputConfigModel.properties?.mapValues {
                objectMapper.treeToValue(it.value, Any::class.java)
            }
            if (!properties.isNullOrEmpty()) {
                existingOutputConfig.updateProperties(properties)
            }
            call.respond(HttpStatusCode.OK, existingOutputConfig.toModel())
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("Missing or malformed id")
            )
            if (outputManager.removeOutput(id)) {
                call.respond(HttpStatusCode.Accepted, SuccessResponse("Output removed"))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("No output with id $id"))
            }
        }
    }
}

fun Application.registerOutputsRoutes() {
    routing {
        outputsRouting()
    }
}
