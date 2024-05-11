package com.example.queriverse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

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

    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 123;
    private boolean showOneTapUI = true;


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

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

    }

    public void loginHandle(View view) {
        emailText = findViewById(R.id.name);
        passwordText = findViewById(R.id.password);

        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "loginHandle: empty");
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            emailText.setError("Please enter email");
            passwordText.setError("Please enter password");
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
                    Intent intent = new Intent(Signin.this, HomePages.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Log.e(TAG, "onException: "+e.getMessage() );
                }
            }
        });
    }

    public void googleHandle(View view) {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                    }
                });


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token." + idToken);
                    Log.d(TAG, "onActivityResult: "+credential.getId());
                    Log.d(TAG, "onActivityResult: "+credential.getDisplayName());

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://queriverse.bytelure.in/api/user/check/"+credential.getId().trim()).build();
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
                                    Toast.makeText(Signin.this, "You are not registered! Please register", Toast.LENGTH_SHORT).show();
                                    emailText.setText("");
                                    passwordText.setText("");
                                });
                                throw new IOException("Unexpected code " + response);
                            }

                            assert response.body() != null;
                            try {
                                JSONObject loginData = new JSONObject(response.body().string());
                                Log.i(TAG, "onResponse: "+loginData.getString("user"));
                                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(SHARED_PREFS_KEY.trim(), loginData.getString("user"));
                                editor.apply();
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(Signin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                });
                                Intent intent = new Intent(Signin.this, HomePages.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException | IOException e) {
                                Log.e(TAG, "onResponseJsonExeption: "+e.getMessage());
                            }

                        }
                    });
                }
            } catch (ApiException e) {
                Log.e(TAG, "onActivityResultException: "+e.getMessage() );
                throw new RuntimeException(e);
            }
        }
    }

    public void signupHandle(View view) {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
        finish();
    }
}