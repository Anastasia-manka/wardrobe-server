package com.wardrobeapp.server.domain.model

import java.util.UUID

data class OutfitItem(
    val id: UUID,
    val itemId: UUID,
    val position: String
)

data class Outfit(
    val id: UUID,
    val userId: UUID,
    val coverUrl: String,
    val styleId: UUID,
    val items: List<OutfitItem>
)