package com.dexter.kycsample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dexter.kycsample.network.*
import com.google.android.material.snackbar.Snackbar
import com.khoslalabs.base.ViKycResults
import com.khoslalabs.facesdk.FaceSdkModuleFactory
import com.khoslalabs.ocrsdk.OcrSdkModuleFactory
import com.khoslalabs.videoidkyc.ui.init.VideoIdKycInitActivity
import com.khoslalabs.videoidkyc.ui.init.VideoIdKycInitRequest
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLHandshakeException


class MainActivity : AppCompatActivity() {
    private lateinit var requestID: String

    companion object {
        const val INIT_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnKYC.setOnClickListener {
            getStatus()
        }
    }

    private fun getStatus() {
        val apiInterface: APIInterface = APIClient.getClient(
            this,
            "https://sandbox.veri5digital.com/"
        )!!.create(APIInterface::class.java)
        apiInterface.getStatus()!!.enqueue(object : Callback<UIDAIResponse?> {
            override fun onResponse(
                call: Call<UIDAIResponse?>?,
                response: Response<UIDAIResponse?>
            ) {
                if (response.isSuccessful) {
                    val statusResponse: UIDAIResponse? = response.body()
                    if (statusResponse != null) {
                        if (statusResponse.status == "SUCCESS") {
                            startSDK()
                        } else {
                            showSnackMessage(rootLayout, "error", statusResponse.message)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UIDAIResponse?>?, t: Throwable) {
                return when (t) {
                    is SSLHandshakeException -> {
                        showSnackMessage(
                            rootLayout,
                            "error",
                            "Your wifi firewall may be blocking your access to our service. Please switch your internet connection"
                        )
                    }
                    is TimeoutException -> {
                        showSnackMessage(
                            rootLayout,
                            "error",
                            "There seems to be an error with your connection"
                        )
                    }
                    is java.net.SocketTimeoutException -> {
                        showSnackMessage(
                            rootLayout,
                            "error",
                            "There seems to be an error with your connection"
                        )
                    }
                    else -> {
                        showSnackMessage(rootLayout, "error", "You are not connected to internet")
                    }
                }
            }
        })
    }

    private fun startSDK() {
        requestID = UUID.randomUUID().toString()
        val hash: String = generateInitialiseHash(requestID)
        val videoIdKycInitRequest = VideoIdKycInitRequest.Builder(
            "OZEL6526",
            "FM63634NF",
            "KYC",
            requestID,
            hash
        )
            .plmaRequired("NO")
            .moduleFactory(OcrSdkModuleFactory.newInstance())
            .moduleFactory(FaceSdkModuleFactory.newInstance())
            .build()

        val myIntent = Intent(this, VideoIdKycInitActivity::class.java)
        myIntent.putExtra("init_request", videoIdKycInitRequest)
        startActivityForResult(myIntent, INIT_REQUEST_CODE)
    }

    private fun generateRequestHash(userId: String): String {
        val md: MessageDigest
        try {
            md = MessageDigest.getInstance("SHA-256")
            val text =
                "OZEL6526|$userId|FM63634NF|r84734475"
            // Change this to UTF-16 if needed
            md.update(text.toByteArray(StandardCharsets.UTF_8))
            val digest: ByteArray = md.digest()
            return java.lang.String.format("%064x", BigInteger(1, digest))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }


    private fun generateInitialiseHash(requestId: String): String {
        //<client_code>|<request_id>|<api_key>|<salt>
        val md: MessageDigest
        try {
            md = MessageDigest.getInstance("SHA-256")
            val text =
                "OZEL6526|$requestId|FM63634NF|r84734475"
            // Change this to UTF-16 if needed
            md.update(text.toByteArray(StandardCharsets.UTF_8))
            val digest: ByteArray = md.digest()
            return java.lang.String.format("%064x", BigInteger(1, digest))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INIT_REQUEST_CODE) {
            if (resultCode == ViKycResults.RESULT_OK || resultCode == ViKycResults.RESULT_DOC_COMPLETE) {
                if (data != null) {
                    val userId = data.getStringExtra("user_id")
                    if (userId != null) {
                        callKycAPI(userId)
                    } else {
                        showSnackMessage(rootLayout, "error", "Null user ID")
                    }
                }
            } else {
                if (data != null) {
                    val error = data.getStringExtra("error_message")
                    if (error != null) {
                        showSnackMessage(rootLayout, "success", error)
                    } else {
                        showSnackMessage(rootLayout, "error", "Some error occurred")
                    }
                }
            }
        }
    }

    private fun callKycAPI(id: String) {
        val hash: String = generateRequestHash(id)
        val apiInterface: APIInterface = APIClient.getClient(
            this,
            "https://sandbox.veri5digital.com/"
        )!!.create(APIInterface::class.java)
        val headersBean = Headers(
            "OZEL6526",
            "OZEL6526",
            "CUSTOMER",
            "ANDROID_SDK",
            requestID,
            "mail",
            "a@b.c",
            "New Delhi",
            System.currentTimeMillis().toString(),
            "TRIAL",
            "192.0.2.0",
            "SELF",
            "4.2.0",
            "REVISED",
            "REVISED"
        )
        apiInterface.postKyc(KYCRequest(headersBean, Request("FM63634NF", id, hash)))!!.enqueue(
            object : Callback<KYCResponse?> {
                override fun onResponse(
                    call: Call<KYCResponse?>?,
                    response: Response<KYCResponse?>
                ) {
                    if (response.isSuccessful) {
                        val kycResponse: KYCResponse? = response.body()
                        if (kycResponse != null) {
                            if (kycResponse.responseStatus.status == "SUCCESS") {
                                val data: ByteArray = Base64.decode(
                                    kycResponse.responseData.kycInfo,
                                    Base64.DEFAULT
                                )
                                val text = String(data, StandardCharsets.UTF_8)
                                showSnackMessage(rootLayout, "success", text)
                            } else {
                                showSnackMessage(
                                    rootLayout,
                                    "error",
                                    kycResponse.responseStatus.message
                                )
                            }
                        } else {
                            showSnackMessage(rootLayout, "error", "Some error occurred")
                        }
                    }
                }

                override fun onFailure(call: Call<KYCResponse?>, t: Throwable) {
                    return when (t) {
                        is SSLHandshakeException -> {
                            showSnackMessage(
                                rootLayout,
                                "error",
                                "Your wifi firewall may be blocking your access to our service. Please switch your internet connection"
                            )
                        }
                        is TimeoutException -> {
                            showSnackMessage(
                                rootLayout,
                                "error",
                                "There seems to be an error with your connection"
                            )
                        }
                        is java.net.SocketTimeoutException -> {
                            showSnackMessage(
                                rootLayout,
                                "error",
                                "There seems to be an error with your connection"
                            )
                        }
                        else -> {
                            showSnackMessage(
                                rootLayout,
                                "error",
                                "You are not connected to internet"
                            )
                        }
                    }
                }
            })
    }

    private fun showSnackMessage(root: View, type: String, message: String) {
        val snackbar: Snackbar = Snackbar
            .make(root, message, Snackbar.LENGTH_LONG)
            .setAction("OK") { }
        snackbar.setActionTextColor(Color.parseColor("#ffffff"))
        val sbView: View = snackbar.view
        when (type) {
            "error" -> sbView.setBackgroundColor(Color.parseColor("#DD6B55"))
            "success" -> sbView.setBackgroundColor(Color.parseColor("#008000"))
        }
        val actionTextView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        val textView: TextView = sbView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 10
        actionTextView.textSize = 16f
        textView.textSize = 16f
        snackbar.show()
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar, event: Int) {}
            override fun onShown(snackbar: Snackbar) {}
        })
    }
}