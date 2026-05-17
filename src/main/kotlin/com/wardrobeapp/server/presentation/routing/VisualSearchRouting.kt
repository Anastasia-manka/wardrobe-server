package com.wardrobeapp.server.presentation.routing

import com.wardrobeapp.server.domain.usecase.ClothingItemUseCase
import com.wardrobeapp.server.ml.EmbeddingService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID
import com.wardrobeapp.server.presentation.dto.toResponse
import com.wardrobeapp.server.ml.DetectionService
import com.wardrobeapp.server.presentation.dto.VisualSearchGroup
import com.wardrobeapp.server.presentation.dto.VisualSearchResponse

fun Route.visualSearchRoutes(
    clothingItemUseCase: ClothingItemUseCase,
    embeddingService: EmbeddingService,
    detectionService: DetectionService
) {
    authenticate("auth-jwt") {
        post("/search/visual") {
            val userId = UUID.fromString(call.principal<JWTPrincipal>()!!.payload.subject)
            val multipart = call.receiveMultipart()
            var imageBytes: ByteArray? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem && part.name == "image") {
                    imageBytes = part.streamProvider().readBytes()
                }
                part.dispose()
            }

            val bytes = imageBytes ?: throw IllegalArgumentException("MISSING_IMAGE")

            val detectedItems = detectionService.detect(bytes)

            if (detectedItems.isEmpty()) {
                val queryEmbedding = embeddingService.computeEmbedding(bytes)
                val results = clothingItemUseCase.findSimilar(userId, queryEmbedding, topN = 10)
                call.respond(HttpStatusCode.OK, VisualSearchResponse(
                    grouped = false,
                    items = results.map { it.toResponse() },
                    groups = emptyList()
                )
                )
                return@post
            }

            val groups = detectedItems.map { detected ->
                val cropEmbedding = embeddingService.computeEmbedding(detected.cropBytes)
                val similar = clothingItemUseCase.findSimilarByCategory(
                    userId = userId,
                    embedding = cropEmbedding,
                    categoryGroupName = detected.className,
                    topN = 5
                )
                VisualSearchGroup(
                    categoryGroup = detected.className,
                    confidence = detected.confidence,
                    items = similar.map { it.toResponse() }
                )
            }.filter { it.items.isNotEmpty() }

            call.respond(HttpStatusCode.OK, VisualSearchResponse(
                grouped = true,
                items = emptyList(),
                groups = groups
            ))
        }
    }
}