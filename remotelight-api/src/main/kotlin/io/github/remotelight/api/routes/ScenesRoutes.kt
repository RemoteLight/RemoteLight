package io.github.remotelight.api.routes

import io.github.remotelight.api.ErrorResponse
import io.github.remotelight.api.SuccessResponse
import io.github.remotelight.api.models.SceneModel
import io.github.remotelight.api.models.toScene
import io.github.remotelight.api.utils.respondIdDefined
import io.github.remotelight.api.utils.respondMissingId
import io.github.remotelight.core.output.scene.SceneManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.scenesRouting() {
    val sceneManager by inject<SceneManager>()

    route("/scenes") {
        get {
            val scenes = sceneManager.getScenes()
            call.respond(scenes)
        }

        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondMissingId()
            val scene = sceneManager.getScene(id) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("No scene with id $id")
            )
            call.respond(scene)
        }

        post {
            val sceneModel = call.receive<SceneModel>()
            if (sceneModel.id != null) {
                return@post call.respondIdDefined()
            }
            val scene = sceneModel.toScene()
            sceneManager.addScene(scene)
            call.respond(HttpStatusCode.Created, scene)
        }

        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondMissingId()
            if (sceneManager.removeScene(id)) {
                call.respond(HttpStatusCode.Accepted, SuccessResponse("Scene removed"))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("No scene with id $id"))
            }
        }
    }
}

fun Application.registerScenesRoutes() {
    routing {
        scenesRouting()
    }
}
