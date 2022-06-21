package com.autoussd.sample.kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.autoussd.AutoUssd
import com.autoussd.models.DeviceSimNetworksListener
import com.autoussd.models.Network
import com.autoussd.models.Result

class PaymentActivity : ComponentActivity() {
    companion object {
        private const val TAG = "PaymentActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // TODO #1: Initialize AutoUssd
        AutoUssd.init(this)

        AutoUssd.getInstance().registerSessionResultListener("callback-key") {
            when (it.status) {
                Result.Status.COMPLETED -> Log.d(TAG, "Completed")
                Result.Status.PARSED -> Log.d(TAG, "Parsed")
                Result.Status.INVALID_SESSION -> Log.d(TAG, "Invalid session Id")
                Result.Status.UNSUPPORTED_SIM -> Log.d(TAG, "Unsupported SIM")
                Result.Status.SESSION_TIMEOUT -> Log.d(TAG, "Session timed-out")
                Result.Status.MENU_CONTENT_MISMATCH -> Log.d(
                    TAG,
                    "USSD content did not match menu content"
                )
                Result.Status.ACCOUNT_SUBSCRIPTION_EXPIRED -> Log.d(
                    TAG,
                    "Account subscription expired"
                )
                Result.Status.UNKNOWN_ERROR -> Log.d(TAG, "Unknown error occurred")
            }
        }

        AutoUssd.getInstance().getDeviceSimNetworks(object : DeviceSimNetworksListener {
            override fun onResult(networks: List<Network>) {
                networks.forEach {
                    Log.d(TAG, it.toString())
                }
            }

            override fun onPermissionDenied() {
                Log.e(TAG, "User denied READ_PHONE_STATE permission")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        // TODO #3 Dispose AutoUssd SDK
        AutoUssd.getInstance().unregisterSessionResultListener("callback-key")
        AutoUssd.getInstance().dispose()
    }

    @Suppress("UNUSED_PARAMETER")
    fun completeTransaction(v: View) {
        // Get references to the EditText components in the view
        val recipientNumberInput = findViewById<EditText>(R.id.numberInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val referenceInput = findViewById<EditText>(R.id.referenceInput)

        // Get the recipient number, amount and reference from the EditText components
        val recipientNumber = recipientNumberInput.text.toString()
        val amount = amountInput.text.toString()
        val reference = referenceInput.text.toString()

        // Check if any of the values are empty and display a toast message if so
        if (recipientNumber.isEmpty() || amount.isEmpty() || reference.isEmpty()) {
            return Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
        }

        // TODO #2: Execute session with the AutoUssd SDK
        AutoUssd.getInstance().executeSession(
            "629e05e1751c102d57c53c0d",
            mapOf(
                "number" to recipientNumber,
                "amount" to amount,
                "reference" to reference,
            ),
            null
        )
    }
}