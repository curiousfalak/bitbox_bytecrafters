package com.example.mltest.aquatic_species_mlmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var speciesResult by remember { mutableStateOf<SpeciesResult?>(null) }

    val uploadedHashes = remember { mutableStateListOf<String>() }

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
                speciesResult = null // Reset previous result
            }
        }
    }

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

                if (uploadedHashes.contains(imageHash)) {
                    Toast.makeText(context, "⚠️ This image has already been uploaded.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val result = tfliteHelper.classify(bmp)
                val maxProb = result.maxOrNull() ?: 0f
                val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1

                if (maxProb < 0.6f || maxIndex !in 0..8) {
                    Toast.makeText(context, "❌ Not a valid aquatic species or low confidence.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val predictedName = labels[maxIndex]
                val confidenceScore = maxProb

                uploadedHashes.add(imageHash)

                speciesResult = SpeciesResult(
                    bitmap = bmp,
                    speciesName = predictedName,
                    confidenceScore = confidenceScore
                )
            }) {
                Text("Classify")
            }
        }

        speciesResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text("Prediction Result", style = typography.titleLarge)

            Image(
                bitmap = result.bitmap!!.asImageBitmap(),
                contentDescription = "Result Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Text("Species: ${result.speciesName}", style = typography.headlineSmall)
            Text("Confidence: ${"%.2f".format(result.confidenceScore * 100)}%", style = typography.bodyLarge)

            // Optionally show details screen below (SpeciesResultScreen)
            SpeciesResultScreen(speciesResult = result)
        }
    }
}
data class SpeciesResult(
    val bitmap: Bitmap?,
    val speciesName: String,
    val confidenceScore: Float
)





@Composable
fun SpeciesResultScreen(speciesResult: SpeciesResult) {
    speciesResult.bitmap?.let { bmp ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Species Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Species: ${speciesResult.speciesName}", style = typography.headlineSmall)
                Text("Confidence: ${"%.2f".format(speciesResult.confidenceScore * 100)}%", style = typography.bodyLarge)
            }
        }
    }
}
