package com.wardrobeapp.server.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val email: String,
    val name: String,
    val gender: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val gender: String
)