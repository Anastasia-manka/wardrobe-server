package com.wardrobeapp.server.domain.model

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val name: String,
    val gender: String,
    val passwordHash: String
)