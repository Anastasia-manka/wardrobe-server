package com.wardrobeapp.server.domain.repository

import com.wardrobeapp.server.domain.model.User
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: UUID): User?
    fun create(email: String, name: String, gender: String, passwordHash: String): User
    fun delete(id: UUID)
    fun update(id: UUID, name: String, gender: String): User
}