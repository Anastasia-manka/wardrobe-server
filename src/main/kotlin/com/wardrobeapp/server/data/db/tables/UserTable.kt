package com.wardrobeapp.server.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UserTable : Table("users") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255)
    val gender = varchar("gender", 50)
    val passwordHash = varchar("password_hash", 255)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}