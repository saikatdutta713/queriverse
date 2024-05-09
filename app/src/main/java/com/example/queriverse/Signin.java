package com.example.queriverse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Signin extends AppCompatActivity {

    public static final String TAG = "Signin log";
    private static final String SHARED_PREFS_NAME = "Queriverse";
    private static final String SHARED_PREFS_KEY = "user";
    private static final int REQUEST_SIGNUP = 0;
    public static String email = null;
    public static String password = null;
    TextView emailText;
    TextView passwordText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void loginHandle(View view) {
        emailText = findViewById(R.id.name);
        passwordText = findViewById(R.id.cpassword);

        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "loginHandle: empty");
            return;
        }
        Log.d(TAG, "loginHandle: "+email+" "+password);
        try {
            login(email, password);

        } catch (JSONException | IOException e) {
            Log.e(TAG, "loginHandleException: "+e.getMessage() );
            throw new RuntimeException(e);
        }
    }

    private void login(String email, String password) throws JSONException, IOException {

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://queriverse.bytelure.in/api/login").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage() );
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    Log.e(TAG, "onResponse: "+response.code() );
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(Signin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        emailText.setText("");
                        passwordText.setText("");
                    });
                    throw new IOException("Unexpected code " + response);
                }
                assert response.body() != null;
                try {
                    JSONObject loginData = new JSONObject(response.body().string());
                    Log.i(TAG, "onResponse: "+loginData);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(Signin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    });
                    String user = loginData.getString("user");

                    SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(SHARED_PREFS_KEY, user);
                    editor.apply();
                    Intent intent = new Intent(Signin.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Log.e(TAG, "onException: "+e.getMessage() );
                }
            }
        });

    }
}