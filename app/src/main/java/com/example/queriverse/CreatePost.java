package com.example.queriverse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePost extends AppCompatActivity {

    private static final String TAG = "createpost log";

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private EditText editText;
    private Uri imageUri;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("Queriverse", MODE_PRIVATE);
        if (!preferences.getAll().containsKey("user")) {
            Intent intent = new Intent(CreatePost.this, Signin.class);
            startActivity(intent);
            finish();
        }
        try {
            JSONObject user = new JSONObject(preferences.getString("user", ""));
            TextView username = findViewById(R.id.username);
            username.setText(user.getString("username"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

//        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.setOnNavigationItemSelectedListener(navListener);
//
//        // Set text color for all states
//        int blackColor = Color.parseColor("#000000"); // Example color (black)
//        bottomNav.setItemTextColor(ColorStateList.valueOf(blackColor));

        editText = findViewById(R.id.editTextCreatePost);
        imageView = findViewById(R.id.imageView);

    }

    public void addPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void createPost(View view) {
        // Validate text field
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            // Show error message if text field is empty
            Toast.makeText(this, "Text field is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get image data if available
        byte[] imageData = null;
        if (imageUri != null) {
            try {
                // Convert image URI to byte array
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                imageData = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "createPostIOException: "+e.getMessage() );
            }
        }

        // Send POST request with OkHttp
        sendPostRequest(text, imageData);
    }

    private void sendPostRequest(String text, byte[] imageData) {
        OkHttpClient client = new OkHttpClient();

        // Create JSON object for request body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", 10);
            jsonObject.put("text", text);
            // Add image data to request body if available
            if (imageData != null) {
                jsonObject.put("image", Base64.encodeToString(imageData, Base64.DEFAULT));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendPostRequestJsonException: "+e.getMessage());
        }

        // Create request body
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        // Create POST request
        Request request = new Request.Builder()
                .url("https://queriverse.bytelure.in/api/posts")
                .post(requestBody)
                .build();

        // Send request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // Handle failure
                Log.e(TAG, "onFailure: "+e.getMessage() );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle response
                assert response.body() != null;
                Log.d(TAG, "onResponse: "+response.code());
//                Log.d(TAG, "onResponse: "+response.body().string());
                editText.setText("");
                imageView.setImageBitmap(null);
                if (response.isSuccessful()) {
                    // Show success message
                    runOnUiThread(() -> Toast.makeText(CreatePost.this, "Post created successfully", Toast.LENGTH_SHORT).show());
                }
                else {
                    // Show error message
                    runOnUiThread(() -> Toast.makeText(CreatePost.this, "Failed to create post", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
//            Log.d(TAG, "onActivityResult: "+imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Log.i(TAG, "onActivityResult: "+bitmap);
                if (bitmap != null) {
                    try {
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onActivityResultException: "+e.getMessage() );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(CreatePost.this, HomePages.class);
        startActivity(intent);
        finish();
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(CreatePost.this, CreatePost.class);
        startActivity(intent);
        finish();
    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(CreatePost.this, Profile.class);
        startActivity(intent);
        finish();
    }
}