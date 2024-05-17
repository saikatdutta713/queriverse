package com.example.queriverse;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Otp extends AppCompatActivity {

    private static final String TAG = "otp log";
    String otp;
    String email;

    EditText otpText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        otpText = findViewById(R.id.otpText);
        email = getIntent().getStringExtra("email");
        Log.d(TAG, "onCreate: " + email);


    }

    public void verifyOtp(View view) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("otp", otpText.getText().toString().trim());
        } catch (Exception e) {
            Log.e(TAG, "verifyOtpJsonException: "+e.getMessage() );
        }
        Request request = new Request.Builder()
                .url("https://queriverse.bytelure.in/api/otp/verify")
                .post(RequestBody.create(jsonObject.toString(), MediaType.parse("application/json")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage() );
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: "+response.code() );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Otp.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Otp.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Otp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Otp.this, "Redirecting...", Toast.LENGTH_SHORT).show();
                            try {
                                sleep(7000);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "SleepException: "+e.getMessage() );
                                throw new RuntimeException(e);
                            }
                            Intent intent = new Intent(Otp.this, Signin.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }
        });
    }

}