package com.example.queriverse;

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
    TextView nameView,emailView,dobView,passView,cpassView;

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

        SharedPreferences sharedPreferences = getSharedPreferences("Queriverse", MODE_PRIVATE);
        String user = sharedPreferences.getString("user",null);
        Log.d(TAG, "onCreate: "+user);

    }

    public void signup(View view) {
        nameView = findViewById(R.id.name);
        emailView = findViewById(R.id.email);
        dobView = findViewById(R.id.dob);
        passView = findViewById(R.id.password);
        cpassView = findViewById(R.id.cpassword);

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
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(Signup.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                });
                Intent intent = new Intent(Signup.this, Signin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}