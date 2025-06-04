package com.example.arduino.utils

import android.hardware.usb.*
import android.content.Context
import android.util.Log
import com.hoho.android.usbserial.driver.*
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.util.concurrent.Executors

class UsbSerialManager(private val context: Context) {
    private var usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var serialPort: UsbSerialPort? = null

    var onDataReceived: ((String) -> Unit)? = null

    fun connect(): Boolean {
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Log.d("UsbSerial", "Nema dostupnih drajvera")
            return false
        }

        val driver = availableDrivers[0]
        val connection = usbManager.openDevice(driver.device) ?: run {
            Log.d("UsbSerial", "Nema permisije ili konekcije")
            return false
        }

        serialPort = driver.ports[0]
        serialPort?.open(connection)
        serialPort?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        Log.d("UsbSerial", "Konektovan serial port")

        return true
    }


    fun sendCommand(command: String) {
        serialPort?.write((command + "\n").toByteArray(), 1000)
    }

    fun close() {
        serialPort?.close()
    }
}
