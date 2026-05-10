package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.usecase.TripUseCase
import com.wardrobeapp.server.presentation.dto.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID
import com.wardrobeapp.server.domain.model.Trip
import com.wardrobeapp.server.domain.model.TripItem

fun Route.tripRoutes(tripUseCase: TripUseCase) {
    authenticate("auth-jwt") {

        post("/trips") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val request = call.receive<CreateTripRequest>()
            val trip = tripUseCase.create(
                userId = userId,
                name = request.name,
                tripDate = request.tripDate,
                tripTypeId = UUID.fromString(request.tripTypeId),
                climateId = UUID.fromString(request.climateId),
                luggageTypeId = UUID.fromString(request.luggageTypeId),
                activityIds = request.activityIds.map { UUID.fromString(it) }
            )
            call.respond(HttpStatusCode.Created, trip.toResponse())
        }

        get("/trips") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val trips = tripUseCase.getAll(userId)
            call.respond(HttpStatusCode.OK, trips.map { it.toResponse() })
        }

        get("/trips/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val trip = tripUseCase.getById(id, userId)
            call.respond(HttpStatusCode.OK, trip.toResponse())
        }

        put("/trips/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            val request = call.receive<UpdateTripRequest>()
            val trip = tripUseCase.update(
                id = id,
                userId = userId,
                name = request.name,
                tripDate = request.tripDate,
                tripTypeId = UUID.fromString(request.tripTypeId),
                climateId = UUID.fromString(request.climateId),
                luggageTypeId = UUID.fromString(request.luggageTypeId),
                activityIds = request.activityIds.map { UUID.fromString(it) }
            )
            call.respond(HttpStatusCode.OK, trip.toResponse())
        }

        delete("/trips/{id}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val id = UUID.fromString(call.parameters["id"])
            tripUseCase.delete(id, userId)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/trips/{id}/items") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val tripId = UUID.fromString(call.parameters["id"])
            val request = call.receive<AddTripItemRequest>()
            val item = tripUseCase.addItem(tripId, UUID.fromString(request.itemId), userId)
            call.respond(HttpStatusCode.Created, TripItemResponse(
                id = item.id.toString(),
                itemId = item.itemId.toString(),
                imageUrl = item.imageUrl,
                categoryName = item.categoryName,
                isPacked = item.isPacked
            ))
        }

        patch("/trips/{id}/items/{itemId}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val tripId = UUID.fromString(call.parameters["id"])
            val itemId = UUID.fromString(call.parameters["itemId"])
            val request = call.receive<UpdateTripItemRequest>()
            tripUseCase.updateItemPacked(tripId, itemId, request.isPacked, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/trips/{id}/items/{itemId}") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val tripId = UUID.fromString(call.parameters["id"])
            val itemId = UUID.fromString(call.parameters["itemId"])
            tripUseCase.removeItem(tripId, itemId, userId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun Trip.toResponse() = TripResponse(
    id = id.toString(),
    userId = userId.toString(),
    name = name,
    tripDate = tripDate,
    tripTypeId = tripTypeId.toString(),
    tripTypeName = tripTypeName,
    climateId = climateId.toString(),
    climateName = climateName,
    luggageTypeId = luggageTypeId.toString(),
    luggageTypeName = luggageTypeName,
    activities = activityIds.zip(activityNames).map { (id, name) ->
        ReferenceItemResponse(id = id.toString(), name = name)
    },
    items = items.map {
        TripItemResponse(
            id = it.id.toString(),
            itemId = it.itemId.toString(),
            imageUrl = it.imageUrl,
            categoryName = it.categoryName,
            isPacked = it.isPacked
        )
    }
)