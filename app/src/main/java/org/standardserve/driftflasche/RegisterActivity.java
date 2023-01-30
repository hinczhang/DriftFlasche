package org.standardserve.driftflasche;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
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
import org.standardserve.driftflasche.login.textValidation;
import org.standardserve.driftflasche.network.NetworkAccess;

import java.io.IOException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText truenameTextView; // true name input field
    private EditText emailTextView; // email input field
    private EditText passwordTextView;  // password input field
    private EditText passwordConfirmTextView; // password confirmation input field
    private Context context; // context of the activity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        init();
    }

    /*
    * initialize the components of the activity
    * */
    private void init(){
        Button registerBtn = findViewById(R.id.registerButton); // register button
        truenameTextView = findViewById(R.id.userTruename); // true name input field
        emailTextView = findViewById(R.id.registertEmailAddress); // email input field
        passwordTextView = findViewById(R.id.editTextPassword); // password input field
        passwordConfirmTextView = findViewById(R.id.editTextPassword_repeat); // password confirmation input field

        // set the text change listener for the true name input field
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

        // set the text change listener for the email input field
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

        // set the text change listener for the password input field
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

        // set the text change listener for the password confirmation input field
        passwordConfirmTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textValidation.repeatPasswordValidation(passwordTextView.getText().toString(), s.toString())) {
                    passwordConfirmTextView.setError("Invalid password");
                }
            }
        });

        // set the click listener for the register button
        registerBtn.setOnClickListener(v -> {
            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();
            String truename = truenameTextView.getText().toString();
            String repeatPassword = passwordConfirmTextView.getText().toString();
            if (textValidation.emailValidation(email) && textValidation.passwordValidation(password) && textValidation.repeatPasswordValidation(password, repeatPassword) && textValidation.truenameValidation(truename)) {
                // if all the input fields are valid, send the registration request to the server
                RootPath.setContext(context);
                String url = NetworkAccess.getLOGINAccess();
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
                        // if the server returns a response, parse the response
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
                        try {
                            assert receiveObj != null;
                            status = receiveObj.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context, "Request Internet Error!", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        RootPath.setContext(context);
                        assert status != null;
                        // if the registration is successful, save the user information to the local file
                        if (Integer.parseInt(status) == 0) {
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