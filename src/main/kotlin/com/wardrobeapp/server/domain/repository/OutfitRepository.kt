package com.wardrobeapp.server.domain.repository

import com.wardrobeapp.server.domain.model.Outfit
import java.util.UUID

interface OutfitRepository {
    fun create(userId: UUID, coverUrl: String, styleId: UUID, items: List<Pair<UUID, String>>): Outfit
    fun findById(id: UUID): Outfit?
    fun findAllByUser(userId: UUID): List<Outfit>
    fun update(id: UUID, coverUrl: String, styleId: UUID, items: List<Pair<UUID, String>>): Outfit
    fun delete(id: UUID)
    fun findByItemId(itemId: UUID): List<Outfit>
}