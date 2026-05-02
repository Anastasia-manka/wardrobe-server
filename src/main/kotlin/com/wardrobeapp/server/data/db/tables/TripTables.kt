package com.wardrobeapp.server.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TripTable : Table("trips") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id)
    val name = varchar("name", 255)
    val tripDate = date("trip_date")
    val tripTypeId = uuid("trip_type_id").references(TripTypeTable.id)
    val climateId = uuid("climate_id").references(ClimateTable.id)
    val luggageTypeId = uuid("luggage_type_id").references(LuggageTypeTable.id)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

object TripActivityTable : Table("trip_activities") {
    val tripId = uuid("trip_id").references(TripTable.id)
    val activityId = uuid("activity_id").references(ActivityTable.id)
    override val primaryKey = PrimaryKey(tripId, activityId)
}

object TripItemTable : Table("trip_items") {
    val id = uuid("id").autoGenerate()
    val tripId = uuid("trip_id").references(TripTable.id)
    val itemId = uuid("item_id").references(ClothingItemTable.id)
    val isPacked = bool("is_packed").default(false)
    override val primaryKey = PrimaryKey(id)
}