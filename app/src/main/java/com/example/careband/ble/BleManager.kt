package com.example.careband.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
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

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        val UUID_FALL_DETECT = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
        val UUID_SERVICE = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
        const val REQUEST_CODE_PERMISSIONS = 1001
    }
    val discoveredDevices = mutableSetOf<String>() // MAC 주소 기준 중복 방지

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun startScan() {
        Log.d("BLE", "startScan() 호출됨")

        if (!hasPermissions()) {
            Log.w("BLE", "권한 없음 -> requestPermissions 호출")
            requestPermissions()
            return
        }

        Log.d("BLE", "권한 있음 -> BLE 스캔 시작")

        try {
            bluetoothAdapter?.bluetoothLeScanner?.let { scanner ->
                val scanSettings = ScanSettings.Builder().build()
                scanner.startScan(null, scanSettings, scanCallback)
                Log.d("BLE", "BLE 스캔 중...")
            }
        } catch (e: SecurityException) {
            Log.e("BLE", "startScan 시 권한 오류", e)
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
            val address = device.address

            if (device.name != null && discoveredDevices.add(address)) {
                Log.d("BLE", "🔍 발견된 기기: ${device.name}, $address")
                onDeviceDiscovered?.invoke(device)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        if (!hasPermissions()) return
        try {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        } catch (e: SecurityException) {
            Log.e("BLE", "connectGatt 오류", e)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            try {
                if (!hasPermissions()) return
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BLE", "BLE 연결 성공")
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BLE", "BLE 연결 해제됨")
                }
            } catch (e: SecurityException) {
                Log.e("BLE", "onConnectionStateChange 오류", e)
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            try {
                val service = gatt.getService(UUID_SERVICE)
                val fallCharacteristic = service?.getCharacteristic(UUID_FALL_DETECT)

                if (fallCharacteristic != null) {
                    gatt.setCharacteristicNotification(fallCharacteristic, true)
                    val descriptor = fallCharacteristic.getDescriptor(
                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    )
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }
                } else {
                    Log.w("BLE", "❌ Characteristic 찾을 수 없음!")
                }
            } catch (e: SecurityException) {
                Log.e("BLE", "onServicesDiscovered 오류", e)
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            try {
                if (!hasPermissions()) return
                val uuid = characteristic.uuid
                val value = characteristic.getStringValue(0)
                Log.d("BLE", "수신된 데이터: $uuid -> $value")

                if (uuid == UUID_FALL_DETECT) {
                    viewModel.updateFromBle(uuid, value)
                }
            } catch (e: SecurityException) {
                Log.e("BLE", "onCharacteristicChanged 오류", e)
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        try {
            if (!hasPermissions()) return
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: SecurityException) {
            Log.e("BLE", "disconnect 오류", e)
        }
    }
}
