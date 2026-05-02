package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.repository.UserRepository
import com.wardrobeapp.server.presentation.dto.ProfileResponse
import com.wardrobeapp.server.presentation.dto.UpdateProfileRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.profileRoutes(userRepository: UserRepository) {
    authenticate("auth-jwt") {

        get("/profile") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val user = userRepository.findById(userId) ?: throw NoSuchElementException("User not found")
            call.respond(HttpStatusCode.OK, ProfileResponse(
                id = user.id.toString(),
                email = user.email,
                name = user.name,
                gender = user.gender
            ))
        }

        put("/profile") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val request = call.receive<UpdateProfileRequest>()
            val user = userRepository.update(userId, request.name, request.gender)
            call.respond(HttpStatusCode.OK, ProfileResponse(
                id = user.id.toString(),
                email = user.email,
                name = user.name,
                gender = user.gender
            ))
        }
    }
}