package com.wardrobeapp.server.domain.usecase

import com.wardrobeapp.server.domain.model.Trip
import com.wardrobeapp.server.domain.model.TripItem
import com.wardrobeapp.server.domain.repository.TripRepository
import java.util.UUID

class TripUseCase(private val repository: TripRepository) {

    fun create(
        userId: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip = repository.create(userId, name, tripDate, tripTypeId, climateId, luggageTypeId, activityIds)

    fun getById(id: UUID, userId: UUID): Trip {
        val trip = repository.findById(id) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        return trip
    }

    fun getAll(userId: UUID): List<Trip> = repository.findAllByUser(userId)

    fun update(
        id: UUID,
        userId: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip {
        val trip = repository.findById(id) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        return repository.update(id, name, tripDate, tripTypeId, climateId, luggageTypeId, activityIds)
    }

    fun delete(id: UUID, userId: UUID) {
        val trip = repository.findById(id) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        repository.delete(id)
    }

    fun addItem(tripId: UUID, itemId: UUID, userId: UUID): TripItem {
        val trip = repository.findById(tripId) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        return repository.addItem(tripId, itemId)
    }

    fun updateItemPacked(tripId: UUID, itemId: UUID, isPacked: Boolean, userId: UUID) {
        val trip = repository.findById(tripId) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        repository.updateItemPacked(tripId, itemId, isPacked)
    }

    fun removeItem(tripId: UUID, itemId: UUID, userId: UUID) {
        val trip = repository.findById(tripId) ?: throw NoSuchElementException("Trip not found")
        if (trip.userId != userId) throw SecurityException("Access denied")
        repository.removeItem(tripId, itemId)
    }
}