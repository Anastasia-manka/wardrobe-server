package com.wardrobeapp.server.domain.usecase

import com.wardrobeapp.server.domain.model.ClothingItem
import com.wardrobeapp.server.domain.repository.ClothingItemRepository
import java.util.UUID

class ClothingItemUseCase(private val repository: ClothingItemRepository) {

    fun create(
        userId: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>
    ): ClothingItem = repository.create(
        userId, imageUrl, categoryId, seasonId, colorId, materialId, storagePlace, comment, labelIds
    )

    fun getById(id: UUID, userId: UUID): ClothingItem {
        val item = repository.findById(id) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        return item
    }

    fun getAll(
        userId: UUID,
        categoryId: UUID?,
        seasonId: UUID?,
        colorId: UUID?,
        materialId: UUID?,
        labelId: UUID?
    ): List<ClothingItem> = repository.findAllByUser(userId, categoryId, seasonId, colorId, materialId, labelId)

    fun update(
        id: UUID,
        userId: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>
    ): ClothingItem {
        val item = repository.findById(id) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        return repository.update(id, imageUrl, categoryId, seasonId, colorId, materialId, storagePlace, comment, labelIds)
    }

    fun delete(id: UUID, userId: UUID) {
        val item = repository.findById(id) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        if (repository.isUsedInOutfitOrTrip(id)) {
            throw IllegalStateException("Item is used in outfit or trip and cannot be deleted")
        }
        repository.delete(id)
    }

    fun createFromTemplates(userId: UUID, templateIds: List<UUID>): List<ClothingItem> =
        repository.createFromTemplates(userId, templateIds)

    fun getCompatible(itemId: UUID, userId: UUID, categoryGroupId: UUID?): List<ClothingItem> {
        val item = repository.findById(itemId) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        return repository.findCompatible(itemId, categoryGroupId)
    }

    fun addCompatibility(itemId: UUID, compatibleItemId: UUID, userId: UUID) {
        val item = repository.findById(itemId) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        repository.addCompatibility(itemId, compatibleItemId)
    }

    fun getOutfitsByItem(itemId: UUID, userId: UUID): List<ClothingItem> {
        val item = repository.findById(itemId) ?: throw NoSuchElementException("Item not found")
        if (item.userId != userId) throw SecurityException("Access denied")
        return repository.findByOutfitId(itemId)
    }
}