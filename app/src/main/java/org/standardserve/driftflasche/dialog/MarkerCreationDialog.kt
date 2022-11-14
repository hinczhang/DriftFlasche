package org.standardserve.driftflasche.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.standardserve.driftflasche.R
import org.standardserve.googletestunit.fileio.RootPath
import org.standardserve.googletestunit.fileio.TokenReadAndWrite
import kotlin.concurrent.thread

enum class BOOTLE_TYPE{
    INFO, MOOD, WARN
}
object MarkerCreationDialog {

    private var bootleType = BOOTLE_TYPE.INFO // 1: info, 2: mood, 3: warn
    private var bootleContent = ""

    private fun setToogleButton(toogleButton: MaterialButtonToggleGroup){
        toogleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when(checkedId) {
                    R.id.info -> {
                        bootleType = BOOTLE_TYPE.INFO
                    }
                    R.id.mood -> {
                        bootleType = BOOTLE_TYPE.MOOD
                    }
                    R.id.warn -> {
                        bootleType = BOOTLE_TYPE.WARN
                    }
                    else -> {
                        bootleType = BOOTLE_TYPE.INFO
                    }
                }
            }
        }
    }

    private fun setDialogText(textView: TextInputEditText){
        textView.addTextChangedListener {
            bootleContent = it.toString()
        }
    }

    fun create(context: Context, username: String, lat: Double, lng: Double, token: String) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.set_bottle, null)
        var toogleButton = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.bottleType)
        var dialogText = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.comment)
        setToogleButton(toogleButton)
        setDialogText(dialogText)
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.bottle_creation_dialog_title).
            setPositiveButton(R.string.confirm) { dialog, which ->
                // Respond to neutral button press
                var url = "http://138.68.65.184:5000/api/login"
                var formData = JSONObject()
                formData.put("mode", "add")
                formData.put("type", bootleType)
                formData.put("username", username)
                formData.put("content", bootleContent)
                formData.put("lat", lat)
                formData.put("lng", lng)
                var strForm = formData.toString()
                val requestBody = strForm?.let {
                    val contentType: MediaType = "application/json".toMediaType()
                    strForm.toRequestBody(contentType)
                } ?: run {
                    FormBody.Builder().build()
                }
                thread {
                    var client = OkHttpClient()
                    var request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()
                    var response = client.newCall(request).execute()
                    var resData = response.body?.string()
                    if(resData != null) {
                        val receiveObj = JSONObject(resData)
                        val status = receiveObj.getString("status")
                        val msg = receiveObj.getString("msg")
                        if (status.toInt() == 0) {
                            Looper.prepare()
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            Looper.loop()
                        }else{
                            Looper.prepare()
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            Looper.loop()
                        }


                    }else{
                        Looper.prepare()
                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show()
                        Looper.loop()
                    }
                }

            }
            .setNegativeButton(R.string.decline) { dialog, which ->
                // Respond to negative button press
            }
            .setView(view)
            .create()
        dialog.show()

    }
}
