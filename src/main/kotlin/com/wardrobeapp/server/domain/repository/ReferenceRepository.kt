package com.wardrobeapp.server.domain.repository

import com.wardrobeapp.server.presentation.dto.ReferencesResponse

interface ReferenceRepository {
    fun getAll(): ReferencesResponse
}