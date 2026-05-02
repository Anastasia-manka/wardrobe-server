package com.wardrobeapp.server.data.db.tables

import org.jetbrains.exposed.sql.Table

object CategoryGroupTable : Table("category_group") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object CategoryTable : Table("category") {
    val id = uuid("id").autoGenerate()
    val groupId = uuid("group_id").references(CategoryGroupTable.id)
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object SeasonTable : Table("season") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object ColorTable : Table("color") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object MaterialTable : Table("material") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object StyleTable : Table("style") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object TripTypeTable : Table("trip_type") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object ClimateTable : Table("climate") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object ActivityTable : Table("activity") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object LuggageTypeTable : Table("luggage_type") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}