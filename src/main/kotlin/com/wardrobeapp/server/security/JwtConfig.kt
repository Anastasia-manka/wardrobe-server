package com.wardrobeapp.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import java.util.*

object JwtConfig {

    private val dotenv = dotenv { ignoreIfMissing = true }
    private val secret = dotenv["JWT_SECRET"]
    private val issuer = "wardrobe-app"
    private val audience = "wardrobe-users"
    private val expirationMs = 30L * 24 * 60 * 60 * 1000

    val realm = "wardrobe"
    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(algorithm)
    }

    fun verifier() = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()
}