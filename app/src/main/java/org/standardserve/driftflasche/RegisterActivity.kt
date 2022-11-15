package org.standardserve.driftflasche

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.standardserve.driftflasche.login.textValidation
import kotlin.concurrent.thread

class RegisterActivity: AppCompatActivity() {
    private lateinit var registerBtn: Button
    private lateinit var truenameTextView: EditText
    private lateinit var emailTextView: EditText
    private lateinit var passwordTextView: EditText
    private lateinit var passwordConfirmTextView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
    }

    private fun init(){
        registerBtn = findViewById(R.id.registerButton)
        truenameTextView = findViewById(R.id.userTruename)
        emailTextView = findViewById(R.id.registertEmailAddress)
        passwordTextView = findViewById(R.id.editTextPassword)
        passwordConfirmTextView = findViewById(R.id.editTextPassword_repeat)

        truenameTextView.addTextChangedListener { truename ->
            try {
                if (!textValidation.truenameValidation(truename.toString())) {
                    truenameTextView.setError("Invalid name")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        emailTextView.addTextChangedListener { email ->
            try {
                if (!textValidation.emailValidation(email.toString())) {
                    emailTextView.setError("Invalid email")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        passwordTextView.addTextChangedListener { password ->
            try {
                if (!textValidation.passwordValidation(password.toString())) {
                    passwordTextView.setError("Invalid password")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        passwordConfirmTextView.addTextChangedListener { password ->
            try {
                if (!textValidation.repeatPasswordValidation(password.toString(), passwordTextView.text.toString())) {
                    passwordConfirmTextView.setError("Invalid password")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        registerBtn.setOnClickListener {
            try {
                if (textValidation.emailValidation(emailTextView.text.toString()) && textValidation.passwordValidation(passwordTextView.text.toString()) && textValidation.repeatPasswordValidation(passwordConfirmTextView.text.toString(), passwordTextView.text.toString()) && textValidation.truenameValidation(truenameTextView.text.toString())){
                    var url = "http://138.68.65.184:5000/api/login"
                    var formData = JSONObject()

                    formData.put("mode", "register")
                    formData.put("username", emailTextView.text.toString())
                    formData.put("password", passwordTextView.text.toString())
                    formData.put("truename", truenameTextView.text.toString())
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
                        var resData = client.newCall(request).execute().body?.string()
                        if(resData!=null) {
                            val receiveObj = JSONObject(resData)
                            val status = receiveObj.getString("status")
                            Looper.prepare()
                            Toast.makeText(this, receiveObj.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                            Looper.loop()
                            if (status.toInt() == 0) {
                                //TODO: register success

                            }


                        }else{
                            Looper.prepare()
                            Toast.makeText(this, "Register failed due to request error", Toast.LENGTH_SHORT).show()
                            Looper.loop()
                        }
                    }
                }
                else {
                    emailTextView.setError("Invalid email")
                    passwordTextView.setError("Invalid password")
                    truenameTextView.setError("Invalid name")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }


    }

}