package com.example.queriverse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class notification extends AppCompatActivity {

    private static final String TAG = "Notification log";

    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("Queriverse", MODE_PRIVATE);
        if (!preferences.getAll().containsKey("user")) {
            Intent intent = new Intent(notification.this, Signin.class);
            startActivity(intent);
            finish();
        }
        try {
            JSONObject user = new JSONObject(preferences.getString("user", ""));
            Log.d(TAG, "onCreate: "+user);
//            userId = user.getInt("id");
        } catch (JSONException e) {
            Log.i(TAG, "onCreateUserException: "+e.getMessage() );
            throw new RuntimeException(e);
        }

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotifications);
        TextView textViewNoNotifications = findViewById(R.id.textViewNoNotifications);

        // Set up RecyclerView layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get notifications from API
        getNotifications(new NotificationsCallback() {
            @Override
            public void onSuccess(List<SingleNotification> notifications) {
                // Set up RecyclerView adapter with received notifications
                NotificationAdapter adapter = new NotificationAdapter(notifications);
                recyclerView.setAdapter(adapter);

                // Show/hide "No notifications" message based on data availability
                if (notifications.isEmpty()) {
                    textViewNoNotifications.setVisibility(View.VISIBLE);
                } else {
                    textViewNoNotifications.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(notification.this, "Failed to fetch notifications: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch notifications from API
    private void getNotifications(final NotificationsCallback callback) {
        String url = "https://queriverse.bytelure.in/api/notifications/"+userId;
        String TAG = "apilog";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    assert response.body() != null;
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    List<SingleNotification> notifications = new ArrayList<>();
                    Log.d(TAG, "onResponse: "+jsonArray);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String type = jsonObject.optString("type");
                        String title = jsonObject.optString("title");
                        String message = jsonObject.optString("message");
                        notifications.add(new SingleNotification(type,title, message));

                        // Post UI update to the main thread using Handler
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(notifications);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onException: "+e.getMessage() );
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(notification.this, HomePages.class);
        startActivity(intent);
        finish();
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(notification.this, CreatePost.class);
        startActivity(intent);
        finish();
    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(notification.this, Profile.class);
        startActivity(intent);
        finish();
    }

    public void playQuiz(View view) {
        Intent intent = new Intent(notification.this, QuizCategory.class);
        startActivity(intent);
    }

    // Callback interface to handle notification retrieval result
    interface NotificationsCallback {
        void onSuccess(List<SingleNotification> notifications);

        void onError(String errorMessage);
    }
}
