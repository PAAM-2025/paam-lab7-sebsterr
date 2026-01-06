package upt.paam.lab7

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
//TODO: 1. Make sure you add permission request to AndroidManifest.xml

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        setContent {
            MaterialTheme {
                NotificationPermissionRequest()
                ServiceControllerUI(

                    onStart = {
                        val intent = Intent(this, MyForegroundService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    },
                    onStop = {
                        stopService(Intent(this, MyForegroundService::class.java))
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "service_channel",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun NotificationPermissionRequest() {
    val context = LocalContext.current

    // Only Android 13+ requires runtime permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        val permissionStatus = ContextCompat.checkSelfPermission(context, permission)

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            // Optional: Toast or log
            // Toast.makeText(context, "Notifications allowed: $granted", Toast.LENGTH_SHORT).show()
        }

        // If not granted, request it
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            SideEffect {
                launcher.launch(permission)
            }
        }
    }
}

@Composable
fun ServiceControllerUI(onStart: () -> Unit, onStop: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onStart) {
            Text("Start Foreground Service")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStop) {
            Text("Stop Service")
        }
    }
}