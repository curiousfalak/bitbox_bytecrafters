package com.example.mltest.geotagged

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GeotaggedScreen() {

    HideSystemBarsEffect()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Build controller
    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    // 2. Bind on launch
    LaunchedEffect(Unit) {
        controller.bindToLifecycle(lifecycleOwner)
    }

    // 3. Make sure CAMERA permission is granted
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(cameraPermission.status) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    // 4. Request LOCATION permission
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(locationPermission.status) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    // 5. Get current location
    var latitude by remember { mutableStateOf("N/A") }
    var longitude by remember { mutableStateOf("N/A") }
    var locationError by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted) {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    locationError = null
                } else {
                    locationError = "Location is not available"
                    Log.e("LocationError", "Location is null")
                }
            }.addOnFailureListener {
                locationError = "Failed to fetch location: ${it.message}"
                Log.e("LocationError", "Error fetching location: ${it.message}")
            }
        }
    }

    // 6. Display the camera preview + geotag (latitude, longitude)
    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CameraWithCaptureAndGeoTag(controller = controller)
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (locationError != null) {
                    BasicText(text = locationError ?: "Fetching location...")
                } else {
                    BasicText(text = "Latitude: $latitude, Longitude: $longitude")
                }
            }

        }
    }
}
