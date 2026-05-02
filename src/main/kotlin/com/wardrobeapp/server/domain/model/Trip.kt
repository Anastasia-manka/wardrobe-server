package com.wardrobeapp.server.domain.model

import java.util.UUID

data class TripItem(
    val id: UUID,
    val itemId: UUID,
    val isPacked: Boolean
)

data class Trip(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val tripDate: String,
    val tripTypeId: UUID,
    val climateId: UUID,
    val luggageTypeId: UUID,
    val activityIds: List<UUID>,
    val items: List<TripItem>
)