package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.usecase.AuthUseCase
import com.wardrobeapp.server.presentation.dto.AuthResponse
import com.wardrobeapp.server.presentation.dto.LoginRequest
import com.wardrobeapp.server.presentation.dto.RegisterRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authUseCase: AuthUseCase) {

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        val (user, token) = authUseCase.register(
            email = request.email,
            name = request.name,
            gender = request.gender,
            password = request.password
        )
        call.respond(HttpStatusCode.Created, AuthResponse(
            token = token,
            userId = user.id.toString(),
            email = user.email,
            name = user.name,
            gender = user.gender
        ))
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        val (user, token) = authUseCase.login(
            email = request.email,
            password = request.password
        )
        call.respond(HttpStatusCode.OK, AuthResponse(
            token = token,
            userId = user.id.toString(),
            email = user.email,
            name = user.name,
            gender = user.gender
        ))
    }

    authenticate("auth-jwt") {
        delete("/auth/account") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.subject
            authUseCase.deleteAccount(userId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}