package com.wardrobeapp.server.data.repository

import com.wardrobeapp.server.data.db.tables.*
import com.wardrobeapp.server.domain.repository.ReferenceRepository
import com.wardrobeapp.server.presentation.dto.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ReferenceRepositoryImpl : ReferenceRepository {

    override fun getAll(): ReferencesResponse = transaction {
        val categoryGroups = CategoryGroupTable.selectAll().map { groupRow ->
            val categories = CategoryTable.selectAll()
                .where { CategoryTable.groupId eq groupRow[CategoryGroupTable.id] }
                .map { catRow ->
                    CategoryItemDto(
                        id = catRow[CategoryTable.id].toString(),
                        name = catRow[CategoryTable.name]
                    )
                }
            CategoryGroupDto(
                id = groupRow[CategoryGroupTable.id].toString(),
                name = groupRow[CategoryGroupTable.name],
                categories = categories
            )
        }

        val seasons = SeasonTable.selectAll().map {
            ReferenceItemDto(it[SeasonTable.id].toString(), it[SeasonTable.name])
        }

        val colors = ColorTable.selectAll().map {
            ReferenceItemDto(it[ColorTable.id].toString(), it[ColorTable.name])
        }

        val materials = MaterialTable.selectAll().map {
            ReferenceItemDto(it[MaterialTable.id].toString(), it[MaterialTable.name])
        }

        val styles = StyleTable.selectAll().map {
            ReferenceItemDto(it[StyleTable.id].toString(), it[StyleTable.name])
        }

        val tripTypes = TripTypeTable.selectAll().map {
            ReferenceItemDto(it[TripTypeTable.id].toString(), it[TripTypeTable.name])
        }

        val climates = ClimateTable.selectAll().map {
            ReferenceItemDto(it[ClimateTable.id].toString(), it[ClimateTable.name])
        }

        val activities = ActivityTable.selectAll().map {
            ReferenceItemDto(it[ActivityTable.id].toString(), it[ActivityTable.name])
        }

        val luggageTypes = LuggageTypeTable.selectAll().map {
            ReferenceItemDto(it[LuggageTypeTable.id].toString(), it[LuggageTypeTable.name])
        }

        val labels = LabelTable.selectAll()
            .where { LabelTable.userId.isNull() }
            .map {
                ReferenceItemDto(it[LabelTable.id].toString(), it[LabelTable.name])
            }

        ReferencesResponse(
            categoryGroups = categoryGroups,
            seasons = seasons,
            colors = colors,
            materials = materials,
            styles = styles,
            tripTypes = tripTypes,
            climates = climates,
            activities = activities,
            luggageTypes = luggageTypes,
            labels = labels
        )
    }
}