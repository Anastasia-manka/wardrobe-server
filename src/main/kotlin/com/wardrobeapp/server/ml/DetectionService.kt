package com.wardrobeapp.server.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.FloatBuffer
import javax.imageio.ImageIO

data class DetectedItem(
    val classId: Int,
    val className: String,
    val confidence: Float,
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val cropBytes: ByteArray
)

class DetectionService {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    private val classes = listOf(
        "Верх", "Верхняя одежда", "Низ", "Платье", "Обувь", "Аксессуары"
    )

    private val confThreshold = 0.25f
    private val iouThreshold  = 0.45f

    init {
        val modelBytes = javaClass.classLoader.getResourceAsStream("wardrobe_detector.onnx")!!.readBytes()
        session = env.createSession(modelBytes)
    }
    @Suppress("UNCHECKED_CAST")
    fun detect(imageBytes: ByteArray): List<DetectedItem> {
        val originalImage = ImageIO.read(ByteArrayInputStream(imageBytes))
        val resized = resizeImage(originalImage, 640, 640)
        val tensor = imageToTensor(resized)

        val inputName = session.inputNames.iterator().next()
        val onnxTensor = OnnxTensor.createTensor(env, tensor, longArrayOf(1, 3, 640, 640))
        val output = session.run(mapOf(inputName to onnxTensor))

        val raw = ((output[0].value) as Array<Array<FloatArray>>)[0]
        onnxTensor.close()
        output.close()

        val boxes = parseOutput(raw, originalImage.width, originalImage.height)
        val filtered = nms(boxes)

        val deduplicated = filtered
            .groupBy { it.className }
            .values
            .map { group -> group.maxByOrNull { it.confidence }!! }

        return deduplicated.map { box ->
            val crop = cropImage(originalImage, box.x1, box.y1, box.x2, box.y2)
            box.copy(cropBytes = crop)
        }
    }

    private fun parseOutput(
        raw: Array<FloatArray>,
        origW: Int,
        origH: Int
    ): List<DetectedItem> {
        val numDetections = raw[0].size
        val results = mutableListOf<DetectedItem>()

        for (i in 0 until numDetections) {
            val cx = raw[0][i]
            val cy = raw[1][i]
            val w  = raw[2][i]
            val h  = raw[3][i]

            val classScores = (4 until 4 + classes.size).map { raw[it][i] }
            val maxScore = classScores.max()
            val classId  = classScores.indexOf(maxScore)

            if (maxScore < confThreshold) continue

            val scaleX = origW / 640f
            val scaleY = origH / 640f

            val x1 = ((cx - w / 2f) * scaleX).coerceIn(0f, origW.toFloat())
            val y1 = ((cy - h / 2f) * scaleY).coerceIn(0f, origH.toFloat())
            val x2 = ((cx + w / 2f) * scaleX).coerceIn(0f, origW.toFloat())
            val y2 = ((cy + h / 2f) * scaleY).coerceIn(0f, origH.toFloat())

            if (x2 <= x1 || y2 <= y1) continue

            results.add(DetectedItem(
                classId   = classId,
                className = classes[classId],
                confidence = maxScore,
                x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                cropBytes = ByteArray(0)
            ))
        }
        return results
    }

    private fun nms(boxes: List<DetectedItem>): List<DetectedItem> {
        val sorted = boxes.sortedByDescending { it.confidence }
        val kept = mutableListOf<DetectedItem>()
        val suppressed = BooleanArray(sorted.size)

        for (i in sorted.indices) {
            if (suppressed[i]) continue
            kept.add(sorted[i])
            for (j in i + 1 until sorted.size) {
                if (suppressed[j]) continue
                if (iou(sorted[i], sorted[j]) > iouThreshold) {
                    suppressed[j] = true
                }
            }
        }
        return kept
    }

    private fun iou(a: DetectedItem, b: DetectedItem): Float {
        val interX1 = maxOf(a.x1, b.x1)
        val interY1 = maxOf(a.y1, b.y1)
        val interX2 = minOf(a.x2, b.x2)
        val interY2 = minOf(a.y2, b.y2)

        val interArea = maxOf(0f, interX2 - interX1) * maxOf(0f, interY2 - interY1)
        val aArea = (a.x2 - a.x1) * (a.y2 - a.y1)
        val bArea = (b.x2 - b.x1) * (b.y2 - b.y1)

        return interArea / (aArea + bArea - interArea + 1e-6f)
    }

    private fun cropImage(image: BufferedImage, x1: Float, y1: Float, x2: Float, y2: Float): ByteArray {
        val ix1 = x1.toInt().coerceIn(0, image.width - 1)
        val iy1 = y1.toInt().coerceIn(0, image.height - 1)
        val ix2 = x2.toInt().coerceIn(ix1 + 1, image.width)
        val iy2 = y2.toInt().coerceIn(iy1 + 1, image.height)
        val crop = image.getSubimage(ix1, iy1, ix2 - ix1, iy2 - iy1)
        val rgb = BufferedImage(crop.width, crop.height, BufferedImage.TYPE_INT_RGB)
        val g = rgb.createGraphics()
        g.drawImage(crop, 0, 0, null)
        g.dispose()
        val baos = java.io.ByteArrayOutputStream()
        ImageIO.write(rgb, "jpg", baos)
        return baos.toByteArray()
    }

    private fun resizeImage(image: BufferedImage, width: Int, height: Int): BufferedImage {
        val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = resized.createGraphics()
        g.drawImage(image, 0, 0, width, height, null)
        g.dispose()
        return resized
    }

    private fun imageToTensor(image: BufferedImage): FloatBuffer {
        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std  = floatArrayOf(0.229f, 0.224f, 0.225f)
        val buffer = FloatBuffer.allocate(3 * 640 * 640)
        for (c in 0..2) {
            for (y in 0 until 640) {
                for (x in 0 until 640) {
                    val pixel = image.getRGB(x, y)
                    val value = when (c) {
                        0 -> ((pixel shr 16) and 0xFF) / 255f
                        1 -> ((pixel shr 8) and 0xFF) / 255f
                        else -> (pixel and 0xFF) / 255f
                    }
                    buffer.put((value - mean[c]) / std[c])
                }
            }
        }
        buffer.rewind()
        return buffer
    }

    fun close() {
        session.close()
        env.close()
    }
}