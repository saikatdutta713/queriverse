package com.example.queriverse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.jar.JarException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Signup extends AppCompatActivity {

    private static final String TAG = "Signup log";
    EditText nameView,emailView,dobView,passView,cpassView;

    String name,email,dob,pass,cpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        dobView = findViewById(R.id.dob);
        passView = findViewById(R.id.password);
        cpassView = findViewById(R.id.cpassword);

        SharedPreferences sharedPreferences = getSharedPreferences("Queriverse", MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);
        Log.d(TAG, "onCreate: "+user);

        EditText passwordEditText = findViewById(R.id.password);
        ImageView eyeIcon = findViewById(R.id.eyeButton);

        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (passwordEditText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    // Password is currently hidden, show it
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_open_eyes); // Change to open eye icon
                } else {
                    // Password is currently visible, hide it
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.ic_eye_closed); // Change to closed eye icon
                }
                // Move cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        cpassView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cpassView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // If the confirm password field is empty, show the text
                    cpassView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                // Move cursor to the end of the text
                cpassView.setSelection(cpassView.getText().length());
            }
        });

        passView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the password meets your criteria
                if (!isValidPassword(s.toString())) {
                    // Display an error message or indicator to the user
                    passView.setError("Password must be at least 8 characters long and contain a combination of capital letters, small letters, numbers, and special characters.");
                } else {
                    // Clear the error message if the password is valid
                    passView.setError(null);
                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        // Password validation criteria
        // Example: At least 8 characters, contains letters, numbers, and special characters
        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }


    public void signup(View view) {

        name = nameView.getText().toString().trim();
        email = emailView.getText().toString().trim();
        dob = dobView.getText().toString().trim();
        pass = passView.getText().toString().trim();
        cpass = cpassView.getText().toString().trim();

        if(name.isEmpty()){
            nameView.setError("Name is required");
        } else if (email.isEmpty()) {
            emailView.setError("Email is required");
        } else if (dob.isEmpty()) {
            dobView.setError("Date of Birth is required");
        } else if (pass.isEmpty()) {
            passView.setError("Password is required");
        } else if (cpass.isEmpty()) {
            cpassView.setError("Confirm Password is required");
        } else if (!pass.equals(cpass)) {
            cpassView.setError("Password does not match");
        }

        JSONObject json = new JSONObject();
        try {
            json.put("username", name);
            json.put("email", email);
            json.put("dob", dob);
            json.put("password", pass);
        } catch (JSONException e) {
            Log.e(TAG, "Exception: "+e.getMessage() );
            throw new RuntimeException(e);
        }
        Log.d(TAG, "json: "+ json);

        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url("https://queriverse.bytelure.in/api/users").post(body).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage() );
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onWrongResponse: "+response.body().string() );
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(response.code() == 409) {
                            Toast.makeText(Signup.this, "User already exists! Please sign in", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Signin.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Signup.this, "Registration Failed! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    throw new IOException("Unexpected code " + response);
                }
                Log.d(TAG, "onResponse: "+response.body().string() );
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", email);
                } catch (JSONException e) {
                    Log.e(TAG, "OnResponseOtpException: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                OkHttpClient client1 = new OkHttpClient();
                RequestBody body1 = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
                Request request1 = new Request.Builder().url("https://queriverse.bytelure.in/api/otp/send").post(body1).build();
                client1.newCall(request1).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "onFailureOtp: "+e.getMessage() );
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        if (!response.isSuccessful()) {
                            Log.d(TAG, "onWrongResponseOtp: "+response.body().string() );
                            throw new IOException("Unexpected code " + response);
                        }
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(Signup.this, "Otp Sent!", Toast.LENGTH_SHORT).show();
                        });
                        Intent intent = new Intent(Signup.this, Otp.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                });

            }
        });
    }

    public void ReirectToSignin(View view) {
        Intent intent = new Intent(Signup.this, Signin.class);
        startActivity(intent);
        finish();
    }
}