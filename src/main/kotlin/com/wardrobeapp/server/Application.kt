package com.wardrobeapp.server

import com.wardrobeapp.server.data.db.DatabaseFactory
import com.wardrobeapp.server.data.db.DatabaseSeeder
import com.wardrobeapp.server.data.repository.ClothingItemRepositoryImpl
import com.wardrobeapp.server.data.repository.OutfitRepositoryImpl
import com.wardrobeapp.server.data.repository.ReferenceRepositoryImpl
import com.wardrobeapp.server.data.repository.TripRepositoryImpl
import com.wardrobeapp.server.data.repository.UserRepositoryImpl
import com.wardrobeapp.server.domain.usecase.AuthUseCase
import com.wardrobeapp.server.domain.usecase.ClothingItemUseCase
import com.wardrobeapp.server.domain.usecase.OutfitUseCase
import com.wardrobeapp.server.domain.usecase.TripUseCase
import com.wardrobeapp.server.ml.DetectionService
import com.wardrobeapp.server.ml.EmbeddingService
import com.wardrobeapp.server.presentation.plugins.configureAuth
import com.wardrobeapp.server.presentation.plugins.configureCors
import com.wardrobeapp.server.presentation.plugins.configureSerialization
import com.wardrobeapp.server.presentation.plugins.configureStatusPages
import com.wardrobeapp.server.presentation.routing.authRoutes
import com.wardrobeapp.server.presentation.routing.clothingItemRoutes
import com.wardrobeapp.server.presentation.routing.outfitRoutes
import com.wardrobeapp.server.presentation.routing.profileRoutes
import com.wardrobeapp.server.presentation.routing.referenceRoutes
import com.wardrobeapp.server.presentation.routing.tripRoutes
import com.wardrobeapp.server.presentation.routing.visualSearchRoutes
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    DatabaseSeeder.seed()

    val embeddingService = EmbeddingService()
    val detectionService = DetectionService()

    val userRepository = UserRepositoryImpl()
    val authUseCase = AuthUseCase(userRepository)
    val referenceRepository = ReferenceRepositoryImpl()
    val clothingItemRepository = ClothingItemRepositoryImpl()
    val clothingItemUseCase = ClothingItemUseCase(clothingItemRepository, embeddingService)
    val outfitRepository = OutfitRepositoryImpl()
    val outfitUseCase = OutfitUseCase(outfitRepository)
    val tripRepository = TripRepositoryImpl()
    val tripUseCase = TripUseCase(tripRepository)

    configureSerialization()
    configureCors()
    configureAuth()
    configureStatusPages()

    environment.monitor.subscribe(ApplicationStopped) {
        embeddingService.close()
        detectionService.close()
    }

    routing {
        authRoutes(authUseCase)
        referenceRoutes(referenceRepository)
        clothingItemRoutes(clothingItemUseCase)
        outfitRoutes(outfitUseCase)
        tripRoutes(tripUseCase)
        profileRoutes(userRepository)
        visualSearchRoutes(clothingItemUseCase, embeddingService, detectionService)
    }
}