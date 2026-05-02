package com.wardrobeapp.server.domain.usecase

import at.favre.lib.crypto.bcrypt.BCrypt
import com.wardrobeapp.server.domain.model.User
import com.wardrobeapp.server.domain.repository.UserRepository
import com.wardrobeapp.server.security.JwtConfig

class AuthUseCase(private val userRepository: UserRepository) {

    fun register(email: String, name: String, gender: String, password: String): Pair<User, String> {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalStateException("User with this email already exists")
        }
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val user = userRepository.create(email, name, gender, passwordHash)
        val token = JwtConfig.generateToken(user.id.toString())
        return Pair(user, token)
    }

    fun login(email: String, password: String): Pair<User, String> {
        val user = userRepository.findByEmail(email)
            ?: throw NoSuchElementException("User not found")
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
        if (!result.verified) {
            throw SecurityException("Invalid password")
        }
        val token = JwtConfig.generateToken(user.id.toString())
        return Pair(user, token)
    }

    fun deleteAccount(userId: String) {
        userRepository.delete(java.util.UUID.fromString(userId))
    }
}