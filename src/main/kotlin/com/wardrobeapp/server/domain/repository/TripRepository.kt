package com.wardrobeapp.server.domain.repository

import com.wardrobeapp.server.domain.model.Trip
import java.util.UUID
import com.wardrobeapp.server.domain.model.TripItem

interface TripRepository {
    fun create(
        userId: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip
    fun findById(id: UUID): Trip?
    fun findAllByUser(userId: UUID): List<Trip>
    fun update(
        id: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip
    fun delete(id: UUID)
    fun addItem(tripId: UUID, itemId: UUID): TripItem
    fun updateItemPacked(tripId: UUID, itemId: UUID, isPacked: Boolean)
    fun removeItem(tripId: UUID, itemId: UUID)
}