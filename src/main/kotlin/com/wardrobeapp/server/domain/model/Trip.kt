package com.wardrobeapp.server.domain.model

import java.util.UUID

data class TripItem(
    val id: UUID,
    val itemId: UUID,
    val imageUrl: String,
    val categoryName: String,
    val isPacked: Boolean
)

data class Trip(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val tripDate: String,
    val tripTypeId: UUID,
    val tripTypeName: String,
    val climateId: UUID,
    val climateName: String,
    val luggageTypeId: UUID,
    val luggageTypeName: String,
    val activityIds: List<UUID>,
    val activityNames: List<String>,
    val items: List<TripItem>
)