package com.roy.unique

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    val PERMISSIONS_REQUEST_READ_PHONE_STATE = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE),
                        PERMISSIONS_REQUEST_READ_PHONE_STATE)
            } else {
                updateInfo()
            }
        } else {
            updateInfo()
        }
    }

    private fun updateInfo() {
        setUpDeviceId()
        setUpSerialId()
        setMacAddress()
        setBleAddress()
        setAndroidId()
        setUuid()
        setAdId()
    }

    private fun setUuid() {
        val uuid = UUID.randomUUID().toString()
        tvUuid.text = uuid
    }

    private fun setAndroidId() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        tvAndroidId.text = androidId
    }

    private fun setBleAddress() {
        var address: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        tvBle.text = address?.address
    }

    @SuppressLint("WifiManagerLeak")
    private fun setMacAddress() {
        val wm = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifi = wm.connectionInfo
        var macAddress = wifi?.macAddress
        tvMac.text = macAddress
    }

    @SuppressLint("MissingPermission")
    private fun setUpSerialId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvSerial.text = Build.getSerial()
        } else {
            tvSerial.text = Build.SERIAL
        }
    }

    private fun setUpDeviceId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tvDeviceId.text = getDeviceImei() ?: ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvDeviceId.text = getDeviceId() ?: ""
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDeviceImei(): String? {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE),
                    PERMISSIONS_REQUEST_READ_PHONE_STATE)
        } else {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.imei
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getDeviceId(): String? {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE),
                    PERMISSIONS_REQUEST_READ_PHONE_STATE)
        } else {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.deviceId
        }
        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setUpDeviceId()
        }
    }

    fun setAdId() {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return
        }

        Thread({
            var idInfo: AdvertisingIdClient.Info? = null
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var advertId: String? = null
            try {
                advertId = idInfo?.id
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            runOnUiThread({
                tvAdverId.text = advertId
            })
        }).start()
    }
}
