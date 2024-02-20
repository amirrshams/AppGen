package com.example.mysendsmsapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val sendSMSPermissionRequestCode = 1
    private var phone: EditText? = null
    private  var message:EditText? = null
    private lateinit var sendBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        phone = findViewById(R.id.phn)
        message = findViewById(R.id.msg)
        sendBtn = findViewById(R.id.btn)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                sendSMSPermissionRequestCode)
        }
        else
            receiveMsg()

        sendBtn.setOnClickListener {
            sendMsg()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == sendSMSPermissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            receiveMsg()
    }

    private fun receiveMsg() {
        val br = object: BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                for (sms: SmsMessage in Telephony.Sms.Intents.getMessagesFromIntent(p1)) {
                    phone?.setText(sms.originatingAddress)
                    message?.setText(sms.displayMessageBody)
                }
            }
        }

        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }



    private fun sendMsg() {
        try {
            val pho = phone?.text.toString()
            val msg = message?.text.toString()

            if (pho.isEmpty() || msg.isEmpty()) {
                Toast.makeText(this@MainActivity, "mobile number and message fields are required", Toast.LENGTH_SHORT).show()
                return
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                val smsManager: SmsManager = if (Build.VERSION.SDK_INT>=23) {
                    //if SDK is greater that or equal to 23 then
                    //this is how we will initialize the SmsManager
                    this.getSystemService(SmsManager::class.java)
                } else{
                    //if user's SDK is less than 23 then
                    //SmsManager will be initialized like this
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                smsManager.sendTextMessage(pho, null, msg, null, null)
                Toast.makeText(this@MainActivity, "Message successfully sent...", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this@MainActivity, "Message failed to deliver", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("MainActivity: ", e.toString())
        }
    }
}