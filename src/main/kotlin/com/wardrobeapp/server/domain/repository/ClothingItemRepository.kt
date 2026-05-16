package com.wardrobeapp.server.domain.repository

import com.wardrobeapp.server.domain.model.ClothingItem
import java.util.UUID

interface ClothingItemRepository {
    fun create(
        userId: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>,
        embedding: String? = null
    ): ClothingItem
    fun findById(id: UUID): ClothingItem?
    fun findAllByUser(
        userId: UUID,
        categoryId: UUID?,
        seasonId: UUID?,
        colorId: UUID?,
        materialId: UUID?,
        labelId: UUID?
    ): List<ClothingItem>
    fun update(
        id: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>
    ): ClothingItem
    fun delete(id: UUID)
    fun updateEmbedding(id: UUID, embedding: String)
    fun isUsedInOutfitOrTrip(id: UUID): Boolean
    fun createFromTemplates(userId: UUID, templateIds: List<UUID>): List<ClothingItem>
    fun findCompatible(itemId: UUID, categoryGroupId: UUID?): List<ClothingItem>
    fun addCompatibility(itemId: UUID, compatibleItemId: UUID)
    fun findByOutfitId(outfitId: UUID): List<ClothingItem>
    fun deleteCompatibility(itemId: UUID, compatibleItemId: UUID)
    fun findSimilar(userId: UUID, queryEmbedding: FloatArray, topN: Int): List<ClothingItem>
}