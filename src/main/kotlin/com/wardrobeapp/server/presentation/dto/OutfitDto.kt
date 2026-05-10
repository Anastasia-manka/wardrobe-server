package com.wardrobeapp.server.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class OutfitItemRequest(
    val itemId: String,
    val position: String
)

@Serializable
data class CreateOutfitRequest(
    val coverUrl: String,
    val styleId: String,
    val items: List<OutfitItemRequest>
)

@Serializable
data class UpdateOutfitRequest(
    val coverUrl: String,
    val styleId: String,
    val items: List<OutfitItemRequest>
)

@Serializable
data class OutfitItemResponse(
    val id: String,
    val itemId: String,
    val position: String
)

@Serializable
data class OutfitResponse(
    val id: String,
    val userId: String,
    val coverUrl: String,
    val styleId: String,
    val styleName: String,
    val items: List<OutfitItemResponse>
)