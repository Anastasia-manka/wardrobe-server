package com.wardrobeapp.server.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateClothingItemRequest(
    val imageUrl: String,
    val categoryId: String,
    val seasonId: String,
    val colorId: String,
    val materialId: String,
    val storagePlace: String? = null,
    val comment: String? = null,
    val labelIds: List<String> = emptyList()
)

@Serializable
data class UpdateClothingItemRequest(
    val imageUrl: String,
    val categoryId: String,
    val seasonId: String,
    val colorId: String,
    val materialId: String,
    val storagePlace: String? = null,
    val comment: String? = null,
    val labelIds: List<String> = emptyList()
)

@Serializable
data class ClothingItemResponse(
    val id: String,
    val userId: String,
    val imageUrl: String,
    val categoryId: String,
    val seasonId: String,
    val colorId: String,
    val materialId: String,
    val storagePlace: String?,
    val comment: String?,
    val labelIds: List<String>
)

@Serializable
data class FromTemplatesRequest(
    val templateIds: List<String>
)

@Serializable
data class AddCompatibilityRequest(
    val compatibleItemId: String
)

@Serializable
data class TemplateItemResponse(
    val id: String,
    val imageUrl: String,
    val categoryId: String,
    val seasonId: String,
    val colorId: String,
    val materialId: String
)