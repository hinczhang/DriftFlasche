package org.standardserve.driftflasche;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.fileio.RootPath;
import org.standardserve.driftflasche.fileio.TokenReadAndWrite;
import org.standardserve.driftflasche.login.textValidation;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText truenameTextView;
    private EditText emailTextView;
    private EditText passwordTextView;
    private EditText passwordConfirmTextView;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        init();
    }

    private void init(){
        registerBtn = findViewById(R.id.registerButton);
        truenameTextView = findViewById(R.id.userTruename);
        emailTextView = findViewById(R.id.registertEmailAddress);
        passwordTextView = findViewById(R.id.editTextPassword);
        passwordConfirmTextView = findViewById(R.id.editTextPassword_repeat);

        truenameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!textValidation.truenameValidation(s.toString())){
                    truenameTextView.setError("Invalid name");
                }
            }
        });

        emailTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.emailValidation(s.toString())) {
                    emailTextView.setError("Invalid email");
                }
            }
        });

        passwordTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.passwordValidation(s.toString())) {
                    passwordTextView.setError("Invalid password");
                }
            }
        });

        passwordConfirmTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.passwordValidation(s.toString())) {
                    passwordConfirmTextView.setError("Invalid password");
                }
            }
        });

        registerBtn.setOnClickListener(v -> {
            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();
            String truename = truenameTextView.getText().toString();
            if (textValidation.emailValidation(email) && textValidation.passwordValidation(password)) {
                RootPath.setContext(context);
                String url = "http://138.68.65.184:5000/api/login";

                OkHttpClient mOKHttpClient = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("mode", "register")
                        .add("username", email)
                        .add("password", password)
                        .add("truename", truename)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = mOKHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String str = response.body().string();
                        if(str != null) {
                            JSONObject receiveObj = null;
                            try {
                                receiveObj = new JSONObject(str);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            String status = null;
                            String msg = null;
                            try {
                                status = receiveObj.getString("status");
                                msg = receiveObj.getString("msg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            RootPath.setContext(context);

                            if (Integer.valueOf(status) == 0) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                Looper.prepare();
                                Toast.makeText(context, "Register success, please login", Toast.LENGTH_SHORT).show();
                                Looper.loop();

                            }
                            else{
                                Looper.prepare();
                                Toast.makeText(context, "Login token check failure, please enter your password", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }else{
                            Looper.prepare();
                            Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }
                });
            } else {
                emailTextView.setError("Invalid email");
                passwordTextView.setError("Invalid password");
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

    }

}