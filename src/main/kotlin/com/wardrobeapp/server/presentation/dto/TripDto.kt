package com.wardrobeapp.server.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTripRequest(
    val name: String,
    val tripDate: String,
    val tripTypeId: String,
    val climateId: String,
    val luggageTypeId: String,
    val activityIds: List<String> = emptyList()
)

@Serializable
data class UpdateTripRequest(
    val name: String,
    val tripDate: String,
    val tripTypeId: String,
    val climateId: String,
    val luggageTypeId: String,
    val activityIds: List<String> = emptyList()
)

@Serializable
data class AddTripItemRequest(
    val itemId: String
)

@Serializable
data class UpdateTripItemRequest(
    val isPacked: Boolean
)

@Serializable
data class TripItemResponse(
    val id: String,
    val itemId: String,
    val imageUrl: String,
    val categoryName: String,
    val isPacked: Boolean
)

@Serializable
data class TripResponse(
    val id: String,
    val userId: String,
    val name: String,
    val tripDate: String,
    val tripTypeId: String,
    val tripTypeName: String,
    val climateId: String,
    val climateName: String,
    val luggageTypeId: String,
    val luggageTypeName: String,
    val activities: List<ReferenceItemResponse>,
    val items: List<TripItemResponse>
)
@Serializable
data class ReferenceItemResponse(
    val id: String,
    val name: String
)