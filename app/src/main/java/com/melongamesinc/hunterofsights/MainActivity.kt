package com.melongamesinc.hunterofsights

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.melongamesinc.hunterofsights.ui.theme.HunterOfSightsTheme
import com.melongamesinc.map.MapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HunterOfSightsTheme {
                LocationPermissionWrapper()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper() {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    when {
        permissionState.allPermissionsGranted -> {
            MapScreen()
        }

        permissionState.shouldShowRationale || !permissionState.allPermissionsGranted -> {
            Text("Geolocation access is required for the application to work.")
        }
    }
}