package org.standardserve.driftflasche

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
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
import org.standardserve.googletestunit.fileio.RootPath
import org.standardserve.googletestunit.fileio.TokenReadAndWrite

import org.standardserve.googletestunit.login.textValidation
import kotlin.concurrent.thread

class LoginActivity:AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var emailView: EditText
    private lateinit var passwordView: EditText
    private lateinit var context: Context
    private lateinit var checkBox: CheckBox
    private lateinit var registerIntent: Intent
    //private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        checkTokenLogin()
        init()
    }

    private fun checkTokenLogin(){
        RootPath.setContext(context)
        val token = TokenReadAndWrite.readToken(RootPath.getCacheDir())
        var url = "http://138.68.65.184:5000/api/login"
        var formData = JSONObject()
        formData.put("mode", "token")
        formData.put("token", token)
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

                RootPath.setContext(this)

                if (status.toInt() == 0) {
                    //TODO: login success
                    TokenReadAndWrite.destroyToken(RootPath.getCacheDir())
                    val token = receiveObj.getString("token")
                    TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token)
                    onSucessJump(receiveObj.getString("username"), token)

                }else{
                    TokenReadAndWrite.destroyToken(RootPath.getCacheDir())
                    Looper.prepare()
                    Toast.makeText(this, "Login token check failure, please enter your password", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }


            }else{
                Looper.prepare()
                Toast.makeText(this, "Login failed due to request error", Toast.LENGTH_SHORT).show()
                Looper.loop()
            }
        }

    }

    private fun init() {
        loginBtn = findViewById(R.id.loginBtn)
        registerBtn = findViewById(R.id.registerBtn)
        emailView = findViewById(R.id.editEmailAddress)
        passwordView = findViewById(R.id.editPassword)
        checkBox = findViewById(R.id.loginKeep)

        emailView.addTextChangedListener { email ->
            try {
                if (!textValidation.emailValidation(email.toString())) {
                    emailView.setError("Invalid email")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        passwordView.addTextChangedListener { password ->
            try {
                if (!textValidation.passwordValidation(password.toString())) {
                    passwordView.setError("Invalid password")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loginBtn.setOnClickListener {
            try {
                if (textValidation.emailValidation(emailView.text.toString()) && textValidation.passwordValidation(passwordView.text.toString())){
                    var url = "http://138.68.65.184:5000/api/login"
                    var formData = JSONObject()
                    var keepOrNot = checkBox.isChecked
                    formData.put("mode", "login")
                    formData.put("username", emailView.text.toString())
                    formData.put("password", passwordView.text.toString())
                    formData.put("keep", keepOrNot)
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

                            //Toast.makeText(context, receiveObj.getString("msg"), Toast.LENGTH_SHORT).show()
                            RootPath.setContext(this)
                            val msg = receiveObj.getString("msg")
                            //Looper.prepare()
                            //Toast.makeText(context, msg + ' ' + status, Toast.LENGTH_SHORT).show()
                            //Looper.loop()
                            //Log.d("Login Status", RootPath.getCacheDir())
                            if (status.toInt() == 0) {
                                //TODO: login success
                                val token = receiveObj.getString("token")
                                TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token)
                                onSucessJump(receiveObj.getString("username"), token)
                            }


                        }else{
                            Looper.prepare()
                            Toast.makeText(this, "Login failed due to request error", Toast.LENGTH_SHORT).show()
                            Looper.loop()
                        }
                    }
                }
                else {
                    emailView.setError("Invalid email")
                    passwordView.setError("Invalid password")
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        registerBtn.setOnClickListener {
            registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }


    }

    private fun onSucessJump(username: String, token: String){
        var mapIntent = Intent(this, MapsActivity::class.java)
        mapIntent.putExtra("username", username)
        mapIntent.putExtra("token", token)
        startActivity(mapIntent)
    }


}