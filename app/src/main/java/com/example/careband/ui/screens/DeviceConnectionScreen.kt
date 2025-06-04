package com.example.careband.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
    var scannedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    val hasPermissions by remember {
        derivedStateOf {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }
    }

    // BLE 기기 스캔 콜백 등록
    DisposableEffect(Unit) {
        bleManager.onDeviceDiscovered = { device ->
            scannedDevice = device
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
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && activity != null) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ),
                        1001
                    )
                }
            }) {
                Text("BLE 권한 요청")
            }
            Text("BLE 권한이 필요합니다.")
            return@Column
        }

        Button(onClick = {
            bleManager.startScan()
        }) {
            Text("스캔 시작")
        }

        scannedDevice?.let { device ->
            Text("발견된 기기: ${device.name ?: "알 수 없음"}")
            Button(onClick = {
                bleManager.connectToDevice(device)
                isConnected = true
            }) {
                Text("연결하기")
            }
        } ?: Text("아직 기기를 찾지 못했습니다")

        if (isConnected) {
            Text("✅ 연결됨", color = MaterialTheme.colorScheme.primary)
        }
    }
}
