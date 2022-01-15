package io.github.remotelight.api.routes

import io.github.remotelight.api.ErrorResponse
import io.github.remotelight.api.SuccessResponse
import io.github.remotelight.api.utils.respondIdDefined
import io.github.remotelight.api.utils.respondMissingId
import io.github.remotelight.controller.model.OutputConfigModel
import io.github.remotelight.controller.model.toModel
import io.github.remotelight.controller.output.OutputController
import io.github.remotelight.controller.output.OutputController.UpdateResult
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.outputsRouting() {
    val outputController by inject<OutputController>()

    route("/outputs") {
        get {
            val outputConfigs = outputController.getOutputConfigsModel()
            call.respond(outputConfigs)
        }

        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondMissingId()
            val outputConfig = outputController.getOutputConfigModelById(id) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("No output with id $id")
            )
            call.respond(outputConfig)
        }

        post {
            val outputConfigModel = call.receive<OutputConfigModel>()
            if (outputConfigModel.id != null) {
                return@post call.respondIdDefined()
            }
            val output = outputController.createOutput(outputConfigModel)
            call.respond(HttpStatusCode.Created, output.config.toModel())
        }

        patch("{id}") {
            val id = call.parameters["id"] ?: return@patch call.respondMissingId()
            val outputConfigModel = call.receive<OutputConfigModel>()

            return@patch when (val updateResult = outputController.updateOutput(id, outputConfigModel)) {
                UpdateResult.InvalidId -> call.respondIdDefined()
                UpdateResult.InvalidOutputIdentifier -> call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("The output identifier cannot be changed.")
                )
                UpdateResult.NotFound -> call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse("No output with id $id")
                )
                is UpdateResult.OutputUpdated -> call.respond(
                    HttpStatusCode.OK,
                    updateResult.outputConfigModel
                )
            }
        }

        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondMissingId()
            if (outputController.removeOutput(id)) {
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
