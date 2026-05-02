package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.repository.ReferenceRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.referenceRoutes(referenceRepository: ReferenceRepository) {
    authenticate("auth-jwt") {
        get("/references") {
            val references = referenceRepository.getAll()
            call.respond(HttpStatusCode.OK, references)
        }
    }
}