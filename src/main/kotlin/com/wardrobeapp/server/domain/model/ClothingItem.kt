package com.wardrobeapp.server.domain.model

import java.util.UUID

data class ClothingItem(
    val id: UUID,
    val userId: UUID,
    val imageUrl: String,
    val categoryId: UUID,
    val seasonId: UUID,
    val colorId: UUID,
    val materialId: UUID,
    val storagePlace: String?,
    val comment: String?,
    val labels: List<UUID>
)