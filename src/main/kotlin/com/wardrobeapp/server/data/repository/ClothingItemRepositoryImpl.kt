package com.wardrobeapp.server.data.repository

import com.wardrobeapp.server.data.db.tables.*
import com.wardrobeapp.server.domain.model.ClothingItem
import com.wardrobeapp.server.domain.repository.ClothingItemRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class ClothingItemRepositoryImpl : ClothingItemRepository {

    override fun create(
        userId: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>,
        embedding: String?
    ): ClothingItem = transaction {
        val id = ClothingItemTable.insert {
            it[ClothingItemTable.userId] = userId
            it[ClothingItemTable.imageUrl] = imageUrl
            it[ClothingItemTable.categoryId] = categoryId
            it[ClothingItemTable.seasonId] = seasonId
            it[ClothingItemTable.colorId] = colorId
            it[ClothingItemTable.materialId] = materialId
            it[ClothingItemTable.storagePlace] = storagePlace
            it[ClothingItemTable.comment] = comment
            it[ClothingItemTable.embedding] = embedding
            it[createdAt] = Clock.System.now()
        } get ClothingItemTable.id

        labelIds.forEach { labelId ->
            ItemLabelTable.insert {
                it[ItemLabelTable.itemId] = id
                it[ItemLabelTable.labelId] = labelId
            }
        }
        findById(id)!!
    }

    override fun findById(id: UUID): ClothingItem? = transaction {
        ClothingItemTable.selectAll()
            .where { ClothingItemTable.id eq id }
            .singleOrNull()
            ?.toClothingItem()
    }

    override fun findAllByUser(
        userId: UUID,
        categoryId: UUID?,
        seasonId: UUID?,
        colorId: UUID?,
        materialId: UUID?,
        labelId: UUID?
    ): List<ClothingItem> = transaction {
        var query = ClothingItemTable.selectAll()
            .where { ClothingItemTable.userId eq userId }

        categoryId?.let { query = query.andWhere { ClothingItemTable.categoryId eq it } }
        seasonId?.let { query = query.andWhere { ClothingItemTable.seasonId eq it } }
        colorId?.let { query = query.andWhere { ClothingItemTable.colorId eq it } }
        materialId?.let { query = query.andWhere { ClothingItemTable.materialId eq it } }

        var items = query.map { it.toClothingItem() }

        labelId?.let { lId ->
            items = items.filter { item -> item.labels.contains(lId) }
        }
        items
    }

    override fun update(
        id: UUID,
        imageUrl: String,
        categoryId: UUID,
        seasonId: UUID,
        colorId: UUID,
        materialId: UUID,
        storagePlace: String?,
        comment: String?,
        labelIds: List<UUID>
    ): ClothingItem = transaction {
        ClothingItemTable.update({ ClothingItemTable.id eq id }) {
            it[ClothingItemTable.imageUrl] = imageUrl
            it[ClothingItemTable.categoryId] = categoryId
            it[ClothingItemTable.seasonId] = seasonId
            it[ClothingItemTable.colorId] = colorId
            it[ClothingItemTable.materialId] = materialId
            it[ClothingItemTable.storagePlace] = storagePlace
            it[ClothingItemTable.comment] = comment
        }
        ItemLabelTable.deleteWhere { ItemLabelTable.itemId eq id }
        labelIds.forEach { labelId ->
            ItemLabelTable.insert {
                it[ItemLabelTable.itemId] = id
                it[ItemLabelTable.labelId] = labelId
            }
        }
        findById(id)!!
    }

    override fun delete(id: UUID) = transaction {
        ItemLabelTable.deleteWhere { ItemLabelTable.itemId eq id }
        ItemCompatibilityTable.deleteWhere { ItemCompatibilityTable.itemId eq id }
        ItemCompatibilityTable.deleteWhere { ItemCompatibilityTable.compatibleItemId eq id }
        ClothingItemTable.deleteWhere { ClothingItemTable.id eq id }
        Unit
    }

    override fun isUsedInOutfitOrTrip(id: UUID): Boolean = transaction {
        val inOutfit = OutfitItemTable.selectAll()
            .where { OutfitItemTable.itemId eq id }
            .count() > 0
        val inTrip = TripItemTable.selectAll()
            .where { TripItemTable.itemId eq id }
            .count() > 0
        inOutfit || inTrip
    }

    override fun createFromTemplates(userId: UUID, templateIds: List<UUID>): List<ClothingItem> = transaction {
        templateIds.mapNotNull { templateId ->
            val template = TemplateItemTable.selectAll()
                .where { TemplateItemTable.id eq templateId }
                .singleOrNull() ?: return@mapNotNull null

            val id = ClothingItemTable.insert {
                it[ClothingItemTable.userId] = userId
                it[imageUrl] = template[TemplateItemTable.imageUrl]
                it[categoryId] = template[TemplateItemTable.categoryId]
                it[seasonId] = template[TemplateItemTable.seasonId]
                it[colorId] = template[TemplateItemTable.colorId]
                it[materialId] = template[TemplateItemTable.materialId]
                it[storagePlace] = null
                it[comment] = null
                it[createdAt] = Clock.System.now()
            } get ClothingItemTable.id

            findById(id)
        }
    }

    override fun findCompatible(itemId: UUID, categoryGroupId: UUID?): List<ClothingItem> = transaction {
        val compatibleIds = ItemCompatibilityTable
            .selectAll()
            .where { ItemCompatibilityTable.itemId eq itemId }
            .map { it[ItemCompatibilityTable.compatibleItemId] }

        if (compatibleIds.isEmpty()) return@transaction emptyList()

        var query = ClothingItemTable.selectAll()
            .where { ClothingItemTable.id inList compatibleIds }

        categoryGroupId?.let { groupId ->
            val categoryIds = CategoryTable
                .selectAll()
                .where { CategoryTable.groupId eq groupId }
                .map { it[CategoryTable.id] }
            query = query.andWhere { ClothingItemTable.categoryId inList categoryIds }
        }

        query.map { it.toClothingItem() }
    }

    override fun addCompatibility(itemId: UUID, compatibleItemId: UUID) = transaction {
        ItemCompatibilityTable.insert {
            it[ItemCompatibilityTable.itemId] = itemId
            it[ItemCompatibilityTable.compatibleItemId] = compatibleItemId
            it[createdAt] = Clock.System.now()
        }
        Unit
    }

    override fun findByOutfitId(outfitId: UUID): List<ClothingItem> = transaction {
        val itemIds = OutfitItemTable
            .selectAll()
            .where { OutfitItemTable.outfitId eq outfitId }
            .map { it[OutfitItemTable.itemId] }

        if (itemIds.isEmpty()) return@transaction emptyList()

        ClothingItemTable.selectAll()
            .where { ClothingItemTable.id inList itemIds }
            .map { it.toClothingItem() }
    }

    override fun deleteCompatibility(itemId: UUID, compatibleItemId: UUID) = transaction {
        ItemCompatibilityTable.deleteWhere {
            (ItemCompatibilityTable.itemId eq itemId) and
                    (ItemCompatibilityTable.compatibleItemId eq compatibleItemId)
        }
        Unit
    }

    private fun ResultRow.toClothingItem(): ClothingItem {
        val id = this[ClothingItemTable.id]
        val labels = ItemLabelTable
            .selectAll()
            .where { ItemLabelTable.itemId eq id }
            .map { it[ItemLabelTable.labelId] }
        return ClothingItem(
            id = id,
            userId = this[ClothingItemTable.userId],
            imageUrl = this[ClothingItemTable.imageUrl],
            categoryId = this[ClothingItemTable.categoryId],
            seasonId = this[ClothingItemTable.seasonId],
            colorId = this[ClothingItemTable.colorId],
            materialId = this[ClothingItemTable.materialId],
            storagePlace = this[ClothingItemTable.storagePlace],
            comment = this[ClothingItemTable.comment],
            labels = labels
        )
    }
    override fun findSimilar(userId: UUID, queryEmbedding: FloatArray, topN: Int): List<ClothingItem> = transaction {
        val vectorLiteral = queryEmbedding.joinToString(",", "[", "]")
        exec(
            """
        SELECT id FROM clothing_items
        WHERE user_id = '$userId'
        AND embedding IS NOT NULL
        ORDER BY embedding::vector <=> '$vectorLiteral'::vector
        LIMIT $topN
        """.trimIndent()
        ) { rs ->
            val ids = mutableListOf<UUID>()
            while (rs.next()) {
                ids.add(UUID.fromString(rs.getString("id")))
            }
            ids
        }?.mapNotNull { findById(it) } ?: emptyList()
    }
    override fun updateEmbedding(id: UUID, embedding: String) = transaction {
        ClothingItemTable.update({ ClothingItemTable.id eq id }) {
            it[ClothingItemTable.embedding] = embedding
        }
        Unit
    }
}