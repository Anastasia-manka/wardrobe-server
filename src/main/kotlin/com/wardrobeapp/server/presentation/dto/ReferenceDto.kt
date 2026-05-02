package com.wardrobeapp.server.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryItemDto(
    val id: String,
    val name: String
)

@Serializable
data class CategoryGroupDto(
    val id: String,
    val name: String,
    val categories: List<CategoryItemDto>
)

@Serializable
data class ReferenceItemDto(
    val id: String,
    val name: String
)

@Serializable
data class ReferencesResponse(
    val categoryGroups: List<CategoryGroupDto>,
    val seasons: List<ReferenceItemDto>,
    val colors: List<ReferenceItemDto>,
    val materials: List<ReferenceItemDto>,
    val styles: List<ReferenceItemDto>,
    val tripTypes: List<ReferenceItemDto>,
    val climates: List<ReferenceItemDto>,
    val activities: List<ReferenceItemDto>,
    val luggageTypes: List<ReferenceItemDto>,
    val labels: List<ReferenceItemDto>
)