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
import com.wardrobeapp.server.data.db.tables.TemplateItemTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import com.wardrobeapp.server.data.db.tables.LabelTable
import com.wardrobeapp.server.presentation.dto.toResponse



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

        get("/items/templates") {
            val items = transaction {
                TemplateItemTable.selectAll().map {
                    TemplateItemResponse(
                        id = it[TemplateItemTable.id].toString(),
                        imageUrl = it[TemplateItemTable.imageUrl],
                        categoryId = it[TemplateItemTable.categoryId].toString(),
                        seasonId = it[TemplateItemTable.seasonId].toString(),
                        colorId = it[TemplateItemTable.colorId].toString(),
                        materialId = it[TemplateItemTable.materialId].toString()
                    )
                }
            }
            call.respond(HttpStatusCode.OK, items)
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
        delete("/items/{id}/compatibility/{compatibleItemId}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val compatibleItemId = UUID.fromString(call.parameters["compatibleItemId"])
            clothingItemUseCase.deleteCompatibility(id, compatibleItemId, userId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
