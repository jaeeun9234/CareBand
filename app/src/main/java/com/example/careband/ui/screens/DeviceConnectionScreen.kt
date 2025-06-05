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
import androidx.compose.foundation.clickable
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
    var connectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val hasPermissions by remember {
        derivedStateOf {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        bleManager.onConnected = { device ->
            isConnected = true
            connectedDevice = device
        }
        bleManager.onDisconnected = {
            isConnected = false
            connectedDevice = null
            selectedDevice = null
        }
    }

    DisposableEffect(Unit) {
        bleManager.onDeviceDiscovered = { device ->
            if (device.name != null && discoveredDevices.none { it.address == device.address }) {
                discoveredDevices.add(device)
            }
        }
        onDispose { bleManager.onDeviceDiscovered = null }
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
            connectedDevice?.let { device ->
                Text("✅ 연결된 기기: ${device.name ?: "이름 없음"}")
                Button(onClick = { bleManager.disconnect() }) {
                    Text("연결 해제")
                }
            } ?: run {
                Button(onClick = {
                    discoveredDevices.clear()
                    bleManager.startScan()
                }) {
                    Text("스캔 시작")
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(discoveredDevices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedDevice = device }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("이름: ${device.name ?: "알 수 없음"}")
                                Text("주소: ${device.address}")
                            }
                        }
                    }
                }

                selectedDevice?.let { device ->
                    Text("선택된 기기: ${device.name ?: "알 수 없음"}")
                    Button(onClick = {
                        bleManager.connectToDevice(device)
                    }) {
                        Text("연결하기")
                    }
                }
            }
        }
    }
}
