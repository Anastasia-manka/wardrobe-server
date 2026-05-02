package com.wardrobeapp.server.data.repository

import com.wardrobeapp.server.data.db.tables.*
import com.wardrobeapp.server.domain.model.Trip
import com.wardrobeapp.server.domain.model.TripItem
import com.wardrobeapp.server.domain.repository.TripRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class TripRepositoryImpl : TripRepository {

    override fun create(
        userId: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip = transaction {
        val id = TripTable.insert {
            it[TripTable.userId] = userId
            it[TripTable.name] = name
            it[TripTable.tripDate] = LocalDate.parse(tripDate)
            it[TripTable.tripTypeId] = tripTypeId
            it[TripTable.climateId] = climateId
            it[TripTable.luggageTypeId] = luggageTypeId
            it[createdAt] = Clock.System.now()
        } get TripTable.id

        activityIds.forEach { activityId ->
            TripActivityTable.insert {
                it[TripActivityTable.tripId] = id
                it[TripActivityTable.activityId] = activityId
            }
        }
        findById(id)!!
    }

    override fun findById(id: UUID): Trip? = transaction {
        TripTable.selectAll()
            .where { TripTable.id eq id }
            .singleOrNull()
            ?.toTrip()
    }

    override fun findAllByUser(userId: UUID): List<Trip> = transaction {
        TripTable.selectAll()
            .where { TripTable.userId eq userId }
            .map { it.toTrip() }
    }

    override fun update(
        id: UUID,
        name: String,
        tripDate: String,
        tripTypeId: UUID,
        climateId: UUID,
        luggageTypeId: UUID,
        activityIds: List<UUID>
    ): Trip = transaction {
        TripTable.update({ TripTable.id eq id }) {
            it[TripTable.name] = name
            it[TripTable.tripDate] = LocalDate.parse(tripDate)
            it[TripTable.tripTypeId] = tripTypeId
            it[TripTable.climateId] = climateId
            it[TripTable.luggageTypeId] = luggageTypeId
        }
        TripActivityTable.deleteWhere { TripActivityTable.tripId eq id }
        activityIds.forEach { activityId ->
            TripActivityTable.insert {
                it[TripActivityTable.tripId] = id
                it[TripActivityTable.activityId] = activityId
            }
        }
        findById(id)!!
    }

    override fun delete(id: UUID) = transaction {
        TripActivityTable.deleteWhere { TripActivityTable.tripId eq id }
        TripItemTable.deleteWhere { TripItemTable.tripId eq id }
        TripTable.deleteWhere { TripTable.id eq id }
        Unit
    }

    override fun addItem(tripId: UUID, itemId: UUID): TripItem = transaction {
        val id = TripItemTable.insert {
            it[TripItemTable.tripId] = tripId
            it[TripItemTable.itemId] = itemId
            it[isPacked] = false
        } get TripItemTable.id
        TripItem(id = id, itemId = itemId, isPacked = false)
    }

    override fun updateItemPacked(tripId: UUID, itemId: UUID, isPacked: Boolean) = transaction {
        TripItemTable.update({
            (TripItemTable.tripId eq tripId) and (TripItemTable.itemId eq itemId)
        }) {
            it[TripItemTable.isPacked] = isPacked
        }
        Unit
    }

    override fun removeItem(tripId: UUID, itemId: UUID) = transaction {
        TripItemTable.deleteWhere {
            (TripItemTable.tripId eq tripId) and (TripItemTable.itemId eq itemId)
        }
        Unit
    }

    private fun ResultRow.toTrip(): Trip {
        val id = this[TripTable.id]
        val activityIds = TripActivityTable
            .selectAll()
            .where { TripActivityTable.tripId eq id }
            .map { it[TripActivityTable.activityId] }
        val items = TripItemTable
            .selectAll()
            .where { TripItemTable.tripId eq id }
            .map {
                TripItem(
                    id = it[TripItemTable.id],
                    itemId = it[TripItemTable.itemId],
                    isPacked = it[TripItemTable.isPacked]
                )
            }
        return Trip(
            id = id,
            userId = this[TripTable.userId],
            name = this[TripTable.name],
            tripDate = this[TripTable.tripDate].toString(),
            tripTypeId = this[TripTable.tripTypeId],
            climateId = this[TripTable.climateId],
            luggageTypeId = this[TripTable.luggageTypeId],
            activityIds = activityIds,
            items = items
        )
    }
}