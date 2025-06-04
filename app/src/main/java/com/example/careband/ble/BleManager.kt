package com.example.careband.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.example.careband.viewmodel.SensorDataViewModel
import java.util.*

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BleManager(
    private val context: Context,
    private val viewModel: SensorDataViewModel
) {
    var onDeviceDiscovered: ((BluetoothDevice) -> Unit)? = null

    private val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        val UUID_FALL_DETECT = UUID.fromString("0000ABCD-0000-1000-8000-00805f9b34fb")
        val UUID_SERVICE = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
        const val REQUEST_CODE_PERMISSIONS = 1001
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun startScan() {
        if (!hasPermissions()) {
            requestPermissions()
            return
        }

        bluetoothAdapter?.bluetoothLeScanner?.let { scanner ->
            val scanFilter = ScanFilter.Builder().build()
            val scanSettings = ScanSettings.Builder().build()
            scanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
        }
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (context is Activity) {
            val permissions = mutableListOf<String>().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(Manifest.permission.BLUETOOTH_SCAN)
                    add(Manifest.permission.BLUETOOTH_CONNECT)
                } else {
                    add(Manifest.permission.BLUETOOTH)
                    add(Manifest.permission.BLUETOOTH_ADMIN)
                    add(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }.toTypedArray()

            ActivityCompat.requestPermissions(context, permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val scanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            Log.d("BLE", "디바이스 발견: ${device.name}")
            onDeviceDiscovered?.invoke(device)  // 🔥 추가: Compose에 전달
            connectToDevice(device)
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        if (!hasPermissions()) return
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (!hasPermissions()) return
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE", "BLE 연결 성공")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "BLE 연결 해제됨")
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (!hasPermissions()) return

            val service = gatt.getService(UUID_SERVICE)
            val fallCharacteristic = service?.getCharacteristic(UUID_FALL_DETECT)

            fallCharacteristic?.let {
                gatt.setCharacteristicNotification(it, true)
                val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor?.let { d ->
                    d.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(d)
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (!hasPermissions()) return
            val uuid = characteristic.uuid
            val value = characteristic.getStringValue(0)
            Log.d("BLE", "수신된 데이터: $uuid -> $value")

            if (uuid == UUID_FALL_DETECT) {
                viewModel.updateFromBle(uuid, value)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        if (!hasPermissions()) return
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}