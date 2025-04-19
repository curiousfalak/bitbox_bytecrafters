package com.example.mltest.aquatic_species_mlmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.nio.ByteBuffer
import java.security.MessageDigest

@Composable
fun ClassifierScreen() {
    val context = LocalContext.current
    val tfliteHelper = remember { TFLiteHelper(context) }

    var bitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var prediction by rememberSaveable { mutableStateOf<FloatArray?>(null) }

    val uploadedHashes = remember { mutableStateListOf<String>() }

    // Updated species list
    val labels = listOf(
        "Black Sea Sprat", "Gilt-Head Bream", "Hourse Mackerel",
        "Red Mullet", "Red Sea Bream", "Sea Bass", "Shrimp",
        "Striped Red Mullet", "Trout"
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it).use { stream ->
                bitmap = BitmapFactory.decodeStream(stream)
                prediction = null
            }
        }
    }

    // Function to create a unique hash for the bitmap
    fun hashBitmap(bitmap: Bitmap): String {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val buffer = ByteBuffer.allocate(resized.byteCount)
        resized.copyPixelsToBuffer(buffer)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(buffer.array())
        return hash.joinToString("") { "%02x".format(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick Image")
        }

        bitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(Modifier.height(8.dp))

            Button(onClick = {
                val imageHash = hashBitmap(bmp)

                // Check if image has already been uploaded based on its hash
                if (uploadedHashes.contains(imageHash)) {
                    Toast.makeText(context, "⚠️ This image has already been uploaded.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val result = tfliteHelper.classify(bmp)
                val maxProb = result.maxOrNull() ?: 0f
                val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1

                // If confidence score is too low or non-aquatic species
                if (maxProb < 0.6f || maxIndex !in 0..8) {
                    Toast.makeText(context, "❌ Thanks for sharing, but it's not an aquatic species or has already been uploaded.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                uploadedHashes.add(imageHash) // Add hash to the list to prevent future duplicacy
                prediction = result
            }) {
                Text("Classify")
            }
        }

        // Display prediction and confidence score
        prediction?.let { probs ->
            val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: 0
            val confidence = probs[maxIndex] * 100

            Text("Prediction: ${labels[maxIndex]}")
            Text("Confidence: ${"%.1f".format(confidence)}%")
        }
    }
}
