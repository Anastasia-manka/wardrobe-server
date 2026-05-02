package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.usecase.ClothingItemUseCase
import com.wardrobeapp.server.presentation.dto.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.clothingItemRoutes(clothingItemUseCase: ClothingItemUseCase) {
    authenticate("auth-jwt") {

        post("/items") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val request = call.receive<CreateClothingItemRequest>()
            val item = clothingItemUseCase.create(
                userId = userId,
                imageUrl = request.imageUrl,
                categoryId = UUID.fromString(request.categoryId),
                seasonId = UUID.fromString(request.seasonId),
                colorId = UUID.fromString(request.colorId),
                materialId = UUID.fromString(request.materialId),
                storagePlace = request.storagePlace,
                comment = request.comment,
                labelIds = request.labelIds.map { UUID.fromString(it) }
            )
            call.respond(HttpStatusCode.Created, item.toResponse())
        }

        get("/items") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val categoryId = call.request.queryParameters["categoryId"]?.let { UUID.fromString(it) }
            val seasonId = call.request.queryParameters["seasonId"]?.let { UUID.fromString(it) }
            val colorId = call.request.queryParameters["colorId"]?.let { UUID.fromString(it) }
            val materialId = call.request.queryParameters["materialId"]?.let { UUID.fromString(it) }
            val labelId = call.request.queryParameters["labelId"]?.let { UUID.fromString(it) }
            val items = clothingItemUseCase.getAll(userId, categoryId, seasonId, colorId, materialId, labelId)
            call.respond(HttpStatusCode.OK, items.map { it.toResponse() })
        }

        get("/items/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val item = clothingItemUseCase.getById(id, userId)
            call.respond(HttpStatusCode.OK, item.toResponse())
        }

        put("/items/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val request = call.receive<UpdateClothingItemRequest>()
            val item = clothingItemUseCase.update(
                id = id,
                userId = userId,
                imageUrl = request.imageUrl,
                categoryId = UUID.fromString(request.categoryId),
                seasonId = UUID.fromString(request.seasonId),
                colorId = UUID.fromString(request.colorId),
                materialId = UUID.fromString(request.materialId),
                storagePlace = request.storagePlace,
                comment = request.comment,
                labelIds = request.labelIds.map { UUID.fromString(it) }
            )
            call.respond(HttpStatusCode.OK, item.toResponse())
        }

        delete("/items/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            clothingItemUseCase.delete(id, userId)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/items/from-templates") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val request = call.receive<FromTemplatesRequest>()
            val items = clothingItemUseCase.createFromTemplates(
                userId = userId,
                templateIds = request.templateIds.map { UUID.fromString(it) }
            )
            call.respond(HttpStatusCode.Created, items.map { it.toResponse() })
        }

        get("/items/{id}/compatible") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val categoryGroupId = call.request.queryParameters["categoryGroupId"]?.let { UUID.fromString(it) }
            val items = clothingItemUseCase.getCompatible(id, userId, categoryGroupId)
            call.respond(HttpStatusCode.OK, items.map { it.toResponse() })
        }

        post("/items/{id}/compatibility") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val request = call.receive<AddCompatibilityRequest>()
            clothingItemUseCase.addCompatibility(id, UUID.fromString(request.compatibleItemId), userId)
            call.respond(HttpStatusCode.Created)
        }
    }
}

private fun com.wardrobeapp.server.domain.model.ClothingItem.toResponse() = ClothingItemResponse(
    id = id.toString(),
    userId = userId.toString(),
    imageUrl = imageUrl,
    categoryId = categoryId.toString(),
    seasonId = seasonId.toString(),
    colorId = colorId.toString(),
    materialId = materialId.toString(),
    storagePlace = storagePlace,
    comment = comment,
    labelIds = labels.map { it.toString() }
)