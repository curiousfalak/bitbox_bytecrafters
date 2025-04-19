package com.example.mltest.aquatic_species_mlmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(context: Context) {

    // 1. Load your model once
    private val interpreter: Interpreter by lazy {
        Interpreter(loadModelFile(context, "aquatic_species_model.tflite"))
    }

    // 2. Utility to memory‑map the .tflite file from assets
    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fd = context.assets.openFd(modelName)
        return FileInputStream(fd.fileDescriptor).channel.map(
            FileChannel.MapMode.READ_ONLY,
            fd.startOffset,
            fd.declaredLength
        )
    }

    // 3. Resize & normalize a Bitmap into [1,224,224,3] FloatArray
    private fun bitmapToInputArray(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val scaled = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = scaled.getPixel(x, y)
                input[0][y][x][0] = Color.red(px)   / 255f
                input[0][y][x][1] = Color.green(px) / 255f
                input[0][y][x][2] = Color.blue(px)  / 255f
            }
        }
        return input
    }

    // 4. Run inference and return the 9‑element output vector
    fun classify(bitmap: Bitmap): FloatArray {
        val input = bitmapToInputArray(bitmap)
        val output = Array(1) { FloatArray(9) }       // your model’s [1,9] output
        interpreter.run(input, output)
        return output[0]                              // return the FloatArray(9)
    }
}
