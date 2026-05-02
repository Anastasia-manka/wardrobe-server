package com.wardrobeapp.server

import com.wardrobeapp.server.data.db.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
}