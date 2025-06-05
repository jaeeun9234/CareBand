// BleManager.kt
package com.example.careband.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.example.careband.viewmodel.SensorDataViewModel
import java.util.*

class BleManager(
    private val context: Context,
    private val viewModel: SensorDataViewModel
) {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null

    var onDeviceDiscovered: ((BluetoothDevice) -> Unit)? = null
    var onConnected: ((BluetoothDevice) -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                Log.d("BLE", "🔍 발견된 기기: ${device.name ?: "이름 없음"}, ${device.address}")
                onDeviceDiscovered?.invoke(device)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        try {
            val scanner = bluetoothAdapter.bluetoothLeScanner ?: return
            Log.d("BLE", "startScan() 호출됨")

            val filters = listOf(
                ScanFilter.Builder().build()
            )
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            Log.d("BLE", "🔵 BLE 스캔 중...")
            scanner.startScan(filters, settings, scanCallback)
        } catch (e: SecurityException) {
            Log.e("BLE", "❌ startScan 권한 오류: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        try {
            val scanner = bluetoothAdapter.bluetoothLeScanner ?: return
            scanner.stopScan(scanCallback)
        } catch (e: SecurityException) {
            Log.e("BLE", "❌ stopScan 권한 오류: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        try {
            bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("BLE", "✅ BLE 연결됨: ${device.address}")
                        onConnected?.invoke(device)
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("BLE", "⚠ BLE 연결 해제됨")
                        onDisconnected?.invoke()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    Log.d("BLE", "📡 GATT 서비스 발견됨")
                }
            })
        } catch (e: SecurityException) {
            Log.e("BLE", "❌ connectGatt 권한 오류: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
            onDisconnected?.invoke()
        } catch (e: SecurityException) {
            Log.e("BLE", "❌ disconnect 권한 오류: ${e.message}")
        }
    }
}
