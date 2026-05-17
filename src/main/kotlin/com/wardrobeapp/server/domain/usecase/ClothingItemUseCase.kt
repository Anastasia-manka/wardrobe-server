package com.wardrobeapp.server.domain.usecase


import com.wardrobeapp.server.domain.model.ClothingItem
import com.wardrobeapp.server.domain.repository.ClothingItemRepository
import com.wardrobeapp.server.ml.DetectionService
import com.wardrobeapp.server.ml.EmbeddingService
import java.util.UUID

class ClothingItemUseCase(
    private val repository: ClothingItemRepository,
    private val embeddingService: EmbeddingService,
    private val detectionService: DetectionService
) {

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
    ): ClothingItem {
        val embedding = try {
            val encodedUrl = imageUrl.replace(" ", "%20")
            val imageBytes = java.net.URI(encodedUrl).toURL().readBytes()
            val vector = computeEmbeddingWithDetection(imageBytes)
            vector.joinToString(",", "[", "]")
        } catch (e: Exception) {
            println("Embedding computation failed: ${e.message}")
            e.printStackTrace()
            null
        }
        return repository.create(
            userId, imageUrl, categoryId, seasonId, colorId, materialId,
            storagePlace, comment, labelIds, embedding
        )
    }

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

    fun createFromTemplates(userId: UUID, templateIds: List<UUID>): List<ClothingItem> {
        val items = repository.createFromTemplates(userId, templateIds)
        Thread {
            items.forEach { item ->
                try {
                    val imageBytes = java.net.URI(item.imageUrl).toURL().readBytes()
                    val vector = computeEmbeddingWithDetection(imageBytes)
                    repository.updateEmbedding(item.id, vector.joinToString(",", "[", "]"))
                } catch (e: Exception) {
                    println("Embedding failed for ${item.id}: ${e.message}")
                }
            }
        }.start()
        return items
    }

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

    fun deleteCompatibility(itemId: UUID, compatibleItemId: UUID, userId: UUID) {
        repository.deleteCompatibility(itemId, compatibleItemId)
    }

    fun findSimilar(userId: UUID, queryEmbedding: FloatArray, topN: Int): List<ClothingItem> =
        repository.findSimilar(userId, queryEmbedding, topN)

    fun findSimilarByCategory(
        userId: UUID,
        embedding: FloatArray,
        categoryGroupName: String,
        topN: Int
    ): List<ClothingItem> =
        repository.findSimilarByCategory(userId, embedding, categoryGroupName, topN)

    fun computeEmbeddingWithDetection(imageBytes: ByteArray): FloatArray {
        val detections = detectionService.detect(imageBytes)
        return if (detections.isNotEmpty()) {
            val best = detections.maxByOrNull { it.confidence }!!
            println("Detection: ${best.className} conf=${best.confidence}")
            embeddingService.computeEmbedding(best.cropBytes)
        } else {
            println("No detections, using full image")
            embeddingService.computeEmbedding(imageBytes)
        }
    }
}