package com.wardrobeapp.server.presentation.dto

import com.wardrobeapp.server.domain.model.ClothingItem
import com.wardrobeapp.server.data.db.tables.LabelTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun ClothingItem.toResponse() = ClothingItemResponse(
    id = id.toString(),
    userId = userId.toString(),
    imageUrl = imageUrl,
    categoryId = categoryId.toString(),
    seasonId = seasonId.toString(),
    colorId = colorId.toString(),
    materialId = materialId.toString(),
    storagePlace = storagePlace,
    comment = comment,
    labels = labels.map { labelId ->
        val row = transaction {
            LabelTable.selectAll()
                .where { LabelTable.id eq labelId }
                .singleOrNull()
        }
        LabelResponse(
            id = labelId.toString(),
            name = row?.get(LabelTable.name) ?: "",
            isCustom = row?.get(LabelTable.userId) != null
        )
    }
)