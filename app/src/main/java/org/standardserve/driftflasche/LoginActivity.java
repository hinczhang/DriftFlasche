package org.standardserve.driftflasche;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.fileio.RootPath;
import org.standardserve.driftflasche.fileio.TokenReadAndWrite;

import org.standardserve.driftflasche.login.textValidation;
import org.standardserve.driftflasche.network.NetworkAccess;

import java.io.IOException;
import java.util.Objects;

/*
* This class is the login activity. It is the first activity that is started when the app is opened.
* */

public class LoginActivity extends AppCompatActivity {
    private EditText emailView; // The email input field
    private EditText passwordView; // The password input field
    private Context context; // The context of the activity
    private CheckBox checkBox; // The checkbox for the remember me function

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        checkTokenLogin();
        init();
    }

    /*
    * This method checks if the user has a token stored on the device. If so, it will try to login with the token.
    * */
    private void checkTokenLogin(){
        RootPath.setContext(context);
        String token = TokenReadAndWrite.readToken(RootPath.getCacheDir());
        String url = NetworkAccess.getLOGINAccess();
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
                // If the response is successful, the user is logged in and the main activity is started.
                String str = Objects.requireNonNull(response.body()).string();
                {
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
                    String token = null;
                    String username = null;
                    String truename = null;
                    try {
                        assert receiveObj != null;
                        status = receiveObj.getString("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    RootPath.setContext(context);
                    assert status != null;
                    // If the status is successful, the token will be stored in the cache directory.
                    if (Integer.parseInt(status) == 0) {
                        try {
                            token = receiveObj.getString("token");
                            username = receiveObj.getString("username");
                            truename = receiveObj.getString("truename");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                        TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token);
                        onSuccessJump(username, token, truename);
                    }else{
                        // If the status is not successful, the user will be notified.
                        TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                        Looper.prepare();
                        Toast.makeText(context, "Login token check failure, please enter your password", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(context, "Login failed due to network error", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    /*
    * Initialize the login activity's components and define the behavior of the components.
    * */

    private void init() {
        // Initialize the components
        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);
        emailView = findViewById(R.id.editEmailAddress);
        passwordView = findViewById(R.id.editPassword);
        checkBox = findViewById(R.id.loginKeep);

        // Email input field behavior
        emailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.emailValidation(s.toString())) {
                    emailView.setError("Invalid email");
                }
            }
        });

        // Password input field behavior
        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.passwordValidation(s.toString())) {
                    passwordView.setError("Invalid password");
                }
            }
        });

        // Login button behavior
        loginBtn.setOnClickListener(v -> {
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();
            boolean keep = checkBox.isChecked();
            // If the email and password are valid, the login request will be sent.
            if (textValidation.emailValidation(email) && textValidation.passwordValidation(password)) {
                RootPath.setContext(context);
                String url = NetworkAccess.getLOGINAccess();

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
                        // If the response is successful, the user is logged in and the main activity is started.
                        String str = Objects.requireNonNull(response.body()).string();
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
                        String token = null;
                        String username = null;
                        String truename = null;
                        try {
                            assert receiveObj != null;
                            status = receiveObj.getString("status");
                            truename = receiveObj.getString("truename");
                            token = receiveObj.getString("token");
                            username = receiveObj.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        RootPath.setContext(context);
                        assert status != null;
                        // If the status is successful, the token will be stored in the cache directory.
                        if (Integer.parseInt(status) == 0) {
                            TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                            TokenReadAndWrite.writeToken(RootPath.getCacheDir(), token);
                            onSuccessJump(username, token, truename);
                        }else{
                            // If the status is not successful, the user will be notified.
                            TokenReadAndWrite.destroyToken(RootPath.getCacheDir());
                            Looper.prepare();
                            Toast.makeText(context, "Login token check failure, please enter your password", Toast.LENGTH_SHORT).show();
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
            // If the user clicks the register button, the register activity will be started.
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
        });

    }

    /*
    * Start the main activity and pass the username and token to the main activity.
    * */

    private void onSuccessJump(String username, String token, String truename){
        Intent mapIntent = new Intent(LoginActivity.this, MapsActivity.class);
        mapIntent.putExtra("username", username);
        mapIntent.putExtra("token", token);
        mapIntent.putExtra("truename", truename);
        startActivity(mapIntent);
    }


}