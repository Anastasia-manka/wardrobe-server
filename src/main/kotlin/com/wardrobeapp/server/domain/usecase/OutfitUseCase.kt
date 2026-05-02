package com.wardrobeapp.server.domain.usecase

import com.wardrobeapp.server.domain.model.Outfit
import com.wardrobeapp.server.domain.repository.OutfitRepository
import java.util.UUID

class OutfitUseCase(private val repository: OutfitRepository) {

    fun create(
        userId: UUID,
        coverUrl: String,
        styleId: UUID,
        items: List<Pair<UUID, String>>
    ): Outfit = repository.create(userId, coverUrl, styleId, items)

    fun getById(id: UUID, userId: UUID): Outfit {
        val outfit = repository.findById(id) ?: throw NoSuchElementException("Outfit not found")
        if (outfit.userId != userId) throw SecurityException("Access denied")
        return outfit
    }

    fun getAll(userId: UUID): List<Outfit> = repository.findAllByUser(userId)

    fun update(
        id: UUID,
        userId: UUID,
        coverUrl: String,
        styleId: UUID,
        items: List<Pair<UUID, String>>
    ): Outfit {
        val outfit = repository.findById(id) ?: throw NoSuchElementException("Outfit not found")
        if (outfit.userId != userId) throw SecurityException("Access denied")
        return repository.update(id, coverUrl, styleId, items)
    }

    fun delete(id: UUID, userId: UUID) {
        val outfit = repository.findById(id) ?: throw NoSuchElementException("Outfit not found")
        if (outfit.userId != userId) throw SecurityException("Access denied")
        repository.delete(id)
    }

    fun getByItemId(itemId: UUID, userId: UUID): List<Outfit> =
        repository.findByItemId(itemId)
}