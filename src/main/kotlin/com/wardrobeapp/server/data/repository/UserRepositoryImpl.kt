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
import com.wardrobeapp.server.data.db.tables.ClothingItemTable
import com.wardrobeapp.server.data.db.tables.ItemLabelTable
import com.wardrobeapp.server.data.db.tables.ItemCompatibilityTable
import com.wardrobeapp.server.data.db.tables.OutfitTable
import com.wardrobeapp.server.data.db.tables.OutfitItemTable
import com.wardrobeapp.server.data.db.tables.TripTable
import com.wardrobeapp.server.data.db.tables.TripActivityTable
import com.wardrobeapp.server.data.db.tables.TripItemTable
import com.wardrobeapp.server.data.db.tables.LabelTable

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
        val userItems = ClothingItemTable
            .selectAll()
            .where { ClothingItemTable.userId eq id }
            .map { it[ClothingItemTable.id] }

        userItems.forEach { itemId ->
            ItemLabelTable.deleteWhere { ItemLabelTable.itemId eq itemId }
            ItemCompatibilityTable.deleteWhere { ItemCompatibilityTable.itemId eq itemId }
            ItemCompatibilityTable.deleteWhere { ItemCompatibilityTable.compatibleItemId eq itemId }
        }

        val userOutfits = OutfitTable
            .selectAll()
            .where { OutfitTable.userId eq id }
            .map { it[OutfitTable.id] }

        userOutfits.forEach { outfitId ->
            OutfitItemTable.deleteWhere { OutfitItemTable.outfitId eq outfitId }
        }

        val userTrips = TripTable
            .selectAll()
            .where { TripTable.userId eq id }
            .map { it[TripTable.id] }

        userTrips.forEach { tripId ->
            TripActivityTable.deleteWhere { TripActivityTable.tripId eq tripId }
            TripItemTable.deleteWhere { TripItemTable.tripId eq tripId }
        }

        OutfitTable.deleteWhere { OutfitTable.userId eq id }
        TripTable.deleteWhere { TripTable.userId eq id }
        ClothingItemTable.deleteWhere { ClothingItemTable.userId eq id }
        LabelTable.deleteWhere { LabelTable.userId eq id }
        UserTable.deleteWhere { UserTable.id eq id }
        Unit
    }

    override fun update(id: UUID, name: String, gender: String): User = transaction {
        UserTable.update({ UserTable.id eq id }) {
            it[UserTable.name] = name
            it[UserTable.gender] = gender
        }
        findById(id)!!
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        name = this[UserTable.name],
        gender = this[UserTable.gender],
        passwordHash = this[UserTable.passwordHash]
    )
}