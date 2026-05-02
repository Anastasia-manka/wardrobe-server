package com.wardrobeapp.server.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OutfitTable : Table("outfits") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id)
    val coverUrl = varchar("cover_url", 512)
    val styleId = uuid("style_id").references(StyleTable.id)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

object OutfitItemTable : Table("outfit_items") {
    val id = uuid("id").autoGenerate()
    val outfitId = uuid("outfit_id").references(OutfitTable.id)
    val itemId = uuid("item_id").references(ClothingItemTable.id)
    val position = text("position")
    override val primaryKey = PrimaryKey(id)
}