package com.wardrobeapp.server.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ClothingItemTable : Table("clothing_items") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id)
    val imageUrl = varchar("image_url", 512)
    val categoryId = uuid("category_id").references(CategoryTable.id)
    val seasonId = uuid("season_id").references(SeasonTable.id)
    val colorId = uuid("color_id").references(ColorTable.id)
    val materialId = uuid("material_id").references(MaterialTable.id)
    val storagePlace = varchar("storage_place", 255).nullable()
    val comment = text("comment").nullable()
    val embedding = text("embedding").nullable()
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

object LabelTable : Table("label") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UserTable.id).nullable()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

object ItemLabelTable : Table("item_labels") {
    val itemId = uuid("item_id").references(ClothingItemTable.id)
    val labelId = uuid("label_id").references(LabelTable.id)
    override val primaryKey = PrimaryKey(itemId, labelId)
}

object ItemCompatibilityTable : Table("item_compatibility") {
    val itemId = uuid("item_id").references(ClothingItemTable.id)
    val compatibleItemId = uuid("compatible_item_id").references(ClothingItemTable.id)
    val createdAt = timestamp("created_at")
    override val primaryKey = PrimaryKey(itemId, compatibleItemId)
}

object TemplateItemTable : Table("template_items") {
    val id = uuid("id").autoGenerate()
    val imageUrl = varchar("image_url", 512)
    val categoryId = uuid("category_id").references(CategoryTable.id)
    val seasonId = uuid("season_id").references(SeasonTable.id)
    val colorId = uuid("color_id").references(ColorTable.id)
    val materialId = uuid("material_id").references(MaterialTable.id)
    override val primaryKey = PrimaryKey(id)
}