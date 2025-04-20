package com.example.mltest.geotagged



import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.media.ExifInterface
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun CameraWithCaptureAndGeoTag(controller: LifecycleCameraController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCapturing by remember { mutableStateOf(false) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var lastImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AndroidView(
            factory = { ctx ->
                androidx.camera.view.PreviewView(ctx).apply {
                    controller.bindToLifecycle(context as LifecycleOwner)
                    this.controller = controller
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (!isCapturing) {
                    isCapturing = true
                    coroutineScope.launch {
                        val location = getCurrentLocation(context)
                        lastLocation = location
                        capturePhotoWithGeoTag(context, controller, location) { uri ->
                            isCapturing = false
                            lastImageUri = uri
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Photo with Geotag")
        }



        lastLocation?.let {
            Text(
                text = "Lat: ${it.latitude}, Lng: ${it.longitude}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun getOutputFile(context: Context): File {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return File(dir, "IMG_$timeStamp.jpg")
}



suspend fun getCurrentLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    return suspendCancellableCoroutine { cont ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    cont.resume(location)
                }
                .addOnFailureListener { e ->
                    cont.resume(null)
                }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }
}


//private fun capturePhotoWithGeoTag(
//    context: Context,
//    controller: LifecycleCameraController,
//    location: Location?,
//    onDone: () -> Unit
//) {
//    val file = getOutputFile(context)
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
//
//    controller.takePicture(
//        outputOptions,
//        ContextCompat.getMainExecutor(context),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                location?.let {
//                    try {
//                        val exif = ExifInterface(file.absolutePath)
//
//                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertToDMS(it.latitude))
//                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (it.latitude >= 0) "N" else "S")
//
//                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertToDMS(it.longitude))
//                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (it.longitude >= 0) "E" else "W")
//
//                        exif.saveAttributes()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                Toast.makeText(context, "Photo saved: ${file.name}", Toast.LENGTH_SHORT).show()
//                onDone()
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//                onDone()
//            }
//        }
//    )
//}
private fun saveImageToGallery(context: Context, file: File): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CameraXApp")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val uri = resolver.insert(collection, values)

    uri?.let {
        resolver.openOutputStream(it).use { outputStream ->
            file.inputStream().copyTo(outputStream!!)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(it, values, null, null)
    }

    return uri
}

private fun capturePhotoWithGeoTag(
    context: Context,
    controller: LifecycleCameraController,
    location: Location?,
    onDone: (Uri?) -> Unit
) {
    val file = getOutputFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    controller.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                location?.let {
                    try {
                        val exif = ExifInterface(file.absolutePath)

                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertToDMS(it.latitude))
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (it.latitude >= 0) "N" else "S")

                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertToDMS(it.longitude))
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (it.longitude >= 0) "E" else "W")

                        exif.saveAttributes()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                val imageUri = saveImageToGallery(context, file)
                Toast.makeText(context, "Photo saved to gallery", Toast.LENGTH_SHORT).show()
                onDone(imageUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                onDone(null)
            }
        }
    )
}

private fun convertToDMS(coordinate: Double): String {
    val absolute = Math.abs(coordinate)
    val degrees = absolute.toInt()
    val minutesDecimal = (absolute - degrees) * 60
    val minutes = minutesDecimal.toInt()
    val seconds = ((minutesDecimal - minutes) * 60)

    return "$degrees/1,$minutes/1,${(seconds * 10000).toInt()}/10000"
}
class MainViewModel: ViewModel(){
    private val _bitmaps= MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps=_bitmaps.asStateFlow()

    fun onTakePhoto(bitmap:Bitmap){
        _bitmaps.value=_bitmaps.value+bitmap
    }

}