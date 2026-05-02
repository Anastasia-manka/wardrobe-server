package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.usecase.OutfitUseCase
import com.wardrobeapp.server.presentation.dto.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.outfitRoutes(outfitUseCase: OutfitUseCase) {
    authenticate("auth-jwt") {

        post("/outfits") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val request = call.receive<CreateOutfitRequest>()
            val outfit = outfitUseCase.create(
                userId = userId,
                coverUrl = request.coverUrl,
                styleId = UUID.fromString(request.styleId),
                items = request.items.map {
                    Pair(UUID.fromString(it.itemId), it.position)
                }
            )
            call.respond(HttpStatusCode.Created, outfit.toResponse())
        }

        get("/outfits") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val outfits = outfitUseCase.getAll(userId)
            call.respond(HttpStatusCode.OK, outfits.map { it.toResponse() })
        }

        get("/outfits/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val outfit = outfitUseCase.getById(id, userId)
            call.respond(HttpStatusCode.OK, outfit.toResponse())
        }

        put("/outfits/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val request = call.receive<UpdateOutfitRequest>()
            val outfit = outfitUseCase.update(
                id = id,
                userId = userId,
                coverUrl = request.coverUrl,
                styleId = UUID.fromString(request.styleId),
                items = request.items.map {
                    Pair(UUID.fromString(it.itemId), it.position)
                }
            )
            call.respond(HttpStatusCode.OK, outfit.toResponse())
        }

        delete("/outfits/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            outfitUseCase.delete(id, userId)
            call.respond(HttpStatusCode.NoContent)
        }

        get("/items/{id}/outfits") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val itemId = UUID.fromString(call.parameters["id"])
            val outfits = outfitUseCase.getByItemId(itemId, userId)
            call.respond(HttpStatusCode.OK, outfits.map { it.toResponse() })
        }
    }
}

private fun com.wardrobeapp.server.domain.model.Outfit.toResponse() = OutfitResponse(
    id = id.toString(),
    userId = userId.toString(),
    coverUrl = coverUrl,
    styleId = styleId.toString(),
    items = items.map {
        OutfitItemResponse(
            id = it.id.toString(),
            itemId = it.itemId.toString(),
            position = it.position
        )
    }
)