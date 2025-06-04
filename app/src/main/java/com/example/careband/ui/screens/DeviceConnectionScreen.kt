package com.example.careband.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.ble.BleManager
import com.example.careband.viewmodel.SensorDataViewModel
import com.example.careband.viewmodel.SensorDataViewModelFactory

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("MissingPermission")
@Composable
fun DeviceConnectionScreen(userId: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    val viewModel: SensorDataViewModel = viewModel(factory = SensorDataViewModelFactory(userId))
    val bleManager = remember { BleManager(context, viewModel) }

    var isConnected by remember { mutableStateOf(false) }
    val scannedDevices = remember { mutableStateListOf<BluetoothDevice>() }

    val hasPermissions by remember {
        derivedStateOf {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }
    }

    DisposableEffect(Unit) {
        bleManager.onDeviceDiscovered = { device ->
            if (!scannedDevices.any { it.address == device.address }) {
                scannedDevices.add(device)
            }
        }
        onDispose {
            bleManager.onDeviceDiscovered = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("BLE 장치 연결", style = MaterialTheme.typography.titleLarge)

        if (!hasPermissions) {
            Text("BLE 권한이 필요합니다. 설정에서 권한을 허용하세요.")
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }) {
                Text("앱 설정으로 이동")
            }
        } else {
            Button(onClick = {
                scannedDevices.clear()
                bleManager.startScan()
            }) {
                Text("스캔 시작")
            }

            if (scannedDevices.isEmpty()) {
                Text("아직 기기를 찾지 못했습니다")
            } else {
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(scannedDevices) { device ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = device.name ?: "이름 없음",
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
                                bleManager.connectToDevice(device)
                                isConnected = true
                            }) {
                                Text("연결")
                            }
                        }
                    }
                }
            }

            if (isConnected) {
                Text("✅ 연결됨", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
