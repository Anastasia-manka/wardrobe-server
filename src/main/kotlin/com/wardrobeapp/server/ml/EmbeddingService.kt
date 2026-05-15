package com.wardrobeapp.server.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.FloatBuffer
import javax.imageio.ImageIO

class EmbeddingService {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        val modelBytes = javaClass.classLoader.getResourceAsStream("wardrobe_final.onnx")!!.readBytes()
        session = env.createSession(modelBytes)
    }

    fun computeEmbedding(imageBytes: ByteArray): FloatArray {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes))
        val resized = resizeImage(image, 224, 224)
        val tensor = imageToTensor(resized)
        val inputName = session.inputNames.iterator().next()
        val onnxTensor = OnnxTensor.createTensor(env, tensor, longArrayOf(1, 3, 224, 224))
        val output = session.run(mapOf(inputName to onnxTensor))
        val embedding = (output[0].value as Array<FloatArray>)[0]
        onnxTensor.close()
        output.close()
        return embedding
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
        val buffer = FloatBuffer.allocate(3 * 224 * 224)
        for (c in 0..2) {
            for (y in 0 until 224) {
                for (x in 0 until 224) {
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