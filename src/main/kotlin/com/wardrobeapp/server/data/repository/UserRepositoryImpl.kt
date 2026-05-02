package com.wardrobeapp.server.data.repository

import com.wardrobeapp.server.data.db.tables.UserTable
import com.wardrobeapp.server.domain.model.User
import com.wardrobeapp.server.domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UserRepositoryImpl : UserRepository {

    override fun findByEmail(email: String): User? = transaction {
        UserTable.selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            ?.toUser()
    }

    override fun findById(id: UUID): User? = transaction {
        UserTable.selectAll()
            .where { UserTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override fun create(email: String, name: String, gender: String, passwordHash: String): User = transaction {
        val newId = java.util.UUID.randomUUID()
        UserTable.insert {
            it[id] = newId
            it[UserTable.email] = email
            it[UserTable.name] = name
            it[UserTable.gender] = gender
            it[UserTable.passwordHash] = passwordHash
            it[createdAt] = kotlinx.datetime.Clock.System.now()
        }
        findById(newId)!!
    }

    override fun delete(id: UUID) = transaction {
        UserTable.deleteWhere { UserTable.id eq id }
        Unit
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        name = this[UserTable.name],
        gender = this[UserTable.gender],
        passwordHash = this[UserTable.passwordHash]
    )
}