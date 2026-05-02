package com.wardrobeapp.server.data.repository

import com.wardrobeapp.server.data.db.tables.OutfitItemTable
import com.wardrobeapp.server.data.db.tables.OutfitTable
import com.wardrobeapp.server.domain.model.Outfit
import com.wardrobeapp.server.domain.model.OutfitItem
import com.wardrobeapp.server.domain.repository.OutfitRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class OutfitRepositoryImpl : OutfitRepository {

    override fun create(
        userId: UUID,
        coverUrl: String,
        styleId: UUID,
        items: List<Pair<UUID, String>>
    ): Outfit = transaction {
        val id = OutfitTable.insert {
            it[OutfitTable.userId] = userId
            it[OutfitTable.coverUrl] = coverUrl
            it[OutfitTable.styleId] = styleId
            it[createdAt] = Clock.System.now()
        } get OutfitTable.id

        items.forEach { (itemId, position) ->
            OutfitItemTable.insert {
                it[OutfitItemTable.outfitId] = id
                it[OutfitItemTable.itemId] = itemId
                it[OutfitItemTable.position] = position
            }
        }
        findById(id)!!
    }

    override fun findById(id: UUID): Outfit? = transaction {
        OutfitTable.selectAll()
            .where { OutfitTable.id eq id }
            .singleOrNull()
            ?.toOutfit()
    }

    override fun findAllByUser(userId: UUID): List<Outfit> = transaction {
        OutfitTable.selectAll()
            .where { OutfitTable.userId eq userId }
            .map { it.toOutfit() }
    }

    override fun update(
        id: UUID,
        coverUrl: String,
        styleId: UUID,
        items: List<Pair<UUID, String>>
    ): Outfit = transaction {
        OutfitTable.update({ OutfitTable.id eq id }) {
            it[OutfitTable.coverUrl] = coverUrl
            it[OutfitTable.styleId] = styleId
        }
        OutfitItemTable.deleteWhere { OutfitItemTable.outfitId eq id }
        items.forEach { (itemId, position) ->
            OutfitItemTable.insert {
                it[OutfitItemTable.outfitId] = id
                it[OutfitItemTable.itemId] = itemId
                it[OutfitItemTable.position] = position
            }
        }
        findById(id)!!
    }

    override fun delete(id: UUID) = transaction {
        OutfitItemTable.deleteWhere { OutfitItemTable.outfitId eq id }
        OutfitTable.deleteWhere { OutfitTable.id eq id }
        Unit
    }

    override fun findByItemId(itemId: UUID): List<Outfit> = transaction {
        val outfitIds = OutfitItemTable
            .selectAll()
            .where { OutfitItemTable.itemId eq itemId }
            .map { it[OutfitItemTable.outfitId] }

        if (outfitIds.isEmpty()) return@transaction emptyList()

        OutfitTable.selectAll()
            .where { OutfitTable.id inList outfitIds }
            .map { it.toOutfit() }
    }

    private fun ResultRow.toOutfit(): Outfit {
        val id = this[OutfitTable.id]
        val items = OutfitItemTable
            .selectAll()
            .where { OutfitItemTable.outfitId eq id }
            .map {
                OutfitItem(
                    id = it[OutfitItemTable.id],
                    itemId = it[OutfitItemTable.itemId],
                    position = it[OutfitItemTable.position]
                )
            }
        return Outfit(
            id = id,
            userId = this[OutfitTable.userId],
            coverUrl = this[OutfitTable.coverUrl],
            styleId = this[OutfitTable.styleId],
            items = items
        )
    }
}