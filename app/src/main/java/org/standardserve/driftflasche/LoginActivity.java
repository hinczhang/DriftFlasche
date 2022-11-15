package org.standardserve.driftflasche;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private Button registerBtn;
    private EditText emailView;
    private EditText passwordView;
    private Context context;
    private CheckBox checkBox;
    private Intent registerIntent;
    //private lateinit var binding: ActivityLoginBinding
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        checkTokenLogin();
        init();
    }

    private void checkTokenLogin(){
        RootPath.setContext(context);
        String token = TokenReadAndWrite.readToken(RootPath.getCacheDir());
        String url = "http://138.68.65.184:5000/api/login";

        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient mOKHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("mode", "token")
                .add("token", token)
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
                    String token = null;
                    String username = null;
                    try {
                        status = receiveObj.getString("status");
                        msg = receiveObj.getString("msg");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    RootPath.setContext(context);

                    if (Integer.valueOf(status) == 0) {
                        //TODO: login success
                        try {
                            token = receiveObj.getString("token");
                            username = receiveObj.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                        TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token);
                        onSucessJump(username, token);
                    }else{
                        TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
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

    }

    private void init() {
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        emailView = findViewById(R.id.editEmailAddress);
        passwordView = findViewById(R.id.editPassword);
        checkBox = findViewById(R.id.loginKeep);

        emailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if (!textValidation.emailValidation(s.toString())) {
                    emailView.setError("Invalid email");
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.emailValidation(s.toString())) {
                    emailView.setError("Invalid email");
                }
            }
        });

        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if (!textValidation.passwordValidation(s.toString())) {
                    passwordView.setError("Invalid password");
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.passwordValidation(s.toString())) {
                    passwordView.setError("Invalid password");
                }
            }
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();
            boolean keep = checkBox.isChecked();
            if (textValidation.emailValidation(email) && textValidation.passwordValidation(password)) {
                RootPath.setContext(context);
                String url = "http://138.68.65.184:5000/api/login";

                OkHttpClient mOKHttpClient = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("mode", "login")
                        .add("username", email)
                        .add("password", password)
                        .add("keep", String.valueOf(keep?1:0))
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
                        Log.e("MarkerCreationDialog", "onResponse: " + str);
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
                            String token = null;
                            String username = null;
                            try {
                                status = receiveObj.getString("status");
                                msg = receiveObj.getString("msg");
                                token = receiveObj.getString("token");
                                username = receiveObj.getString("username");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            RootPath.setContext(context);

                            if (Integer.valueOf(status) == 0) {
                                //TODO: login success
                                TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                                TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token);
                                onSucessJump(username, token);
                            }else{
                                TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
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
                emailView.setError("Invalid email");
                passwordView.setError("Invalid password");
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void onSucessJump(String username, String token){
        Intent mapIntent = new Intent(LoginActivity.this, MapsActivity.class);
        mapIntent.putExtra("username", username);
        mapIntent.putExtra("token", token);
        startActivity(mapIntent);
    }


}