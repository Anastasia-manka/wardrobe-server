package com.wardrobeapp.server

import com.wardrobeapp.server.data.db.DatabaseFactory
import com.wardrobeapp.server.data.db.DatabaseSeeder

import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    DatabaseSeeder.seed()
}