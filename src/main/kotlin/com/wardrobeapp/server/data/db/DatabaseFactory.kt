package com.wardrobeapp.server.data.db

import com.wardrobeapp.server.data.db.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DatabaseFactory {

    fun init() {
        val dotenv = dotenv { ignoreIfMissing = true }
        val dbUri = URI(dotenv["DATABASE_URL"].replace("postgresql://", "http://"))

        val userInfo = dbUri.userInfo.split(":")
        val username = userInfo[0]
        val password = userInfo[1]
        val host = dbUri.host
        val port = if (dbUri.port == -1) 5432 else dbUri.port
        val database = dbUri.path.removePrefix("/")

        val jdbcUrl = "jdbc:postgresql://$host:$port/$database?sslmode=require"

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        Database.connect(HikariDataSource(config))
        createTables()
    }

    private fun createTables() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                CategoryGroupTable,
                CategoryTable,
                SeasonTable,
                ColorTable,
                MaterialTable,
                StyleTable,
                TripTypeTable,
                ClimateTable,
                ActivityTable,
                LuggageTypeTable,
                UserTable,
                ClothingItemTable,
                LabelTable,
                ItemLabelTable,
                ItemCompatibilityTable,
                TemplateItemTable,
                OutfitTable,
                OutfitItemTable,
                TripTable,
                TripActivityTable,
                TripItemTable
            )
        }
    }
}