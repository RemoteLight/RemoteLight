package io.github.remotelight.api.utils

import io.github.remotelight.api.ErrorResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondMissingId() = this.respond(
    HttpStatusCode.BadRequest,
    ErrorResponse("Missing or malformed id")
)

suspend fun ApplicationCall.respondIdDefined() = this.respond(
    HttpStatusCode.BadRequest,
    ErrorResponse("The id must not be defined")
)
