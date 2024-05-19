//HomePages.java

package com.example.queriverse;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.queriverse.adapters.UserPostAdapter;
import com.example.queriverse.data.UserPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomePages extends AppCompatActivity {

    private final List<UserPost> userPostList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_pages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("Queriverse", MODE_PRIVATE);
        if (!preferences.getAll().containsKey("user")) {
            Intent intent = new Intent(HomePages.this, Signin.class);
            startActivity(intent);
            finish();
        }

        recyclerView = findViewById(R.id.recycler_view_user_list);
        requestQueue = Volley.newRequestQueue(this);



        // Check if the app was opened via a deep link
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri deepLink = intent.getData();
            handleDeepLink(deepLink);
        } else {
            fetchDataFromServer();
        }
        // Check if the app is installed from the Play Store
        boolean appInstalled = isAppInstalled("com.example.queriverse");

        // If the app is not installed, redirect the user to the Play Store
        if (!appInstalled) {
            redirectToPlayStore();
        }

    }
    // Inside the HomePages class
    private void handleDeepLink(Uri deepLink) {
        // Extract information from the deep link and navigate the user accordingly
        // Example: Extract postId from the deep link and open the corresponding post
        String postId = deepLink.getLastPathSegment();
        // Open the post with postId
    }

    private void fetchDataFromServer() {
            String url = "https://queriverse.bytelure.in/api/posts";

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response ->  {
                        parseResponse(response);
                        UserPostAdapter userPostAdapter = new UserPostAdapter(userPostList, this, ""); // Pass an empty string or the appropriate value
                        recyclerView.setAdapter(userPostAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HomePages.this));
                    },
                    error -> {
                        // Handle error response
                    }
            );
            // Add the request to the RequestQueue
            requestQueue.add(jsonArrayRequest);
        }

    // Inside the parseResponse method
    private void parseResponse(JSONArray response) {
        Log.i("Postlog", "parseResponse: " + response);
        try {
            for (int i = 0; i < response.length(); i++) {
                Log.i("response", "response Length" + i);
                JSONObject postObject = response.getJSONObject(i);
                String userId = postObject.getString("user"); // user ID is fetched here
                String postId = postObject.getString("id"); // Fetch postId from JSON response
                String postCreatedAt = postObject.getString("created_at");

                final String postDescription = truncateDescription(postObject.getString("text"));
                String postImageUrl = postObject.optString("image"); // Set a default image or load from URL
                final String postLikes;
                final String postDislikes;
                final String postComments;

                // Check if "likes" array exists and is not null
                if (postObject.has("likes") && !postObject.isNull("likes")) {
                    postLikes = String.valueOf(postObject.getJSONArray("likes").length());
                } else {
                    postLikes = "0";
                }

                // Check if "dislikes" array exists and is not null
                if (postObject.has("dislikes") && !postObject.isNull("dislikes")) {
                    postDislikes = String.valueOf(postObject.getJSONArray("dislikes").length());
                } else {
                    postDislikes = "0";
                }

                // Check if "comments" array exists and is not null
                if (postObject.has("comments") && !postObject.isNull("comments")) {
                    postComments = String.valueOf(postObject.getJSONArray("comments").length());
                } else {
                    postComments = "0";
                }

                // Calculate time elapsed since post creation
                long elapsedTimeMillis = System.currentTimeMillis() - getTimeInMillis(postCreatedAt);
                long elapsedHours = elapsedTimeMillis / (60 * 60 * 1000);
                String postDate;
                if (elapsedHours < 24) {
                    // Less than 24 hours, display time only
                    postDate = getTimeOnly(postCreatedAt);
                } else {
                    // More than 24 hours, display date only
                    postDate = getDateOnly(postCreatedAt);
                }

                // Fetch username, image based on user ID
                fetchUserData(userId, new UserDataCallback() {
                    @Override
                    public void onSuccess(String username, String profileImage) {
                        // Create UserPost object and add it to the list
                        UserPost userPost = new UserPost(postId, userId, profileImage, username, postDate, postDescription, postImageUrl, postLikes, postDislikes, postComments);
                        userPost.setPostId(postId); // Set postId for each UserPost object
                        userPostList.add(userPost);

                        // Notify adapter of data change
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // Handle error
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("JSONParsingError", "Error parsing JSON response", e);
        }
    }






    // Method to limit the length of the post description and append "(see more)" if needed
    private String truncateDescription(String description) {
        if (description.length() > 200) {
            return description.substring(0, 197) + "..." + " (see More)";
        } else {
            return description;
        }
    }


    // Helper method to convert ISO 8601 formatted date string to milliseconds
    private long getTimeInMillis(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                return date.getTime();
            } else {
                // Handle the case when the date is null
                Log.e("getTimeInMillis", "Date object is null");
                return 0;
            }
        } catch (ParseException e) {
            Log.e("getTimeInMillis", "Error parsing date: " + e.getMessage());
            // Handle the ParseException
            return 0;
        }
    }

    // Helper method to extract time from ISO 8601 formatted date string
    private String getTimeOnly(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("'Today at' HH:mm", Locale.getDefault());
                return timeFormat.format(date);
            } else {
                Log.e("ParseException", "Parsed date is null for input: " + dateString);
                return "";
            }
        } catch (ParseException e) {
            Log.e("ParseException", "Error parsing date: " + dateString + ", Message: " + e.getMessage());
            return "";
        }
    }


    // Helper method to extract date from ISO 8601 formatted date string
    private String getDateOnly(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                return dateFormat.format(date);
            } else {
                Log.e("ParseException", "Parsed date is null for input: " + dateString);
                return "";
            }
        } catch (ParseException e) {
            Log.e("ParseException", "Error parsing date: " + dateString + ", Message: " + e.getMessage());
            return "";
        }
    }




    // Method to fetch userdata based on user ID
    private void fetchUserData(String userId, final UserDataCallback callback) {
        String userUrl = "https://queriverse.bytelure.in/api/users/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                userUrl,
                null,
                response -> {
                    try {
                        String username = response.getString("username");
                        String authorImage = response.getString("picture");
                        callback.onSuccess(username, authorImage);
                    } catch (JSONException e) {
                        Log.e("UserDataError", "Error parsing JSON response: " + e.getMessage());
                        callback.onError(null);
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error fetching user data: " + error.getMessage());
                    callback.onError(error);
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void onNotificationClick(View view) {
        Intent intent = new Intent(HomePages.this, notification.class);
        startActivity(intent);
    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(HomePages.this, HomePages.class);
        startActivity(intent);
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(HomePages.this, CreatePost.class);
        startActivity(intent);

    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(HomePages.this, Profile.class);
        startActivity(intent);
    }

    public void playQuiz(View view) {
        Intent intent = new Intent(HomePages.this, QuizCategory.class);
        startActivity(intent);
    }


    interface UserDataCallback {
        void onSuccess(String username, String authorImage);

        void onError(VolleyError error);
    }

    private boolean isAppInstalled (String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
   private void redirectToPlayStore() {
    Uri uri = Uri.parse("market://details?id=com.example.queriverse");
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
    } else {
        // Handle the case where the Play Store app is not available
        // You can open the Play Store website instead
        Uri playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=com.example.queriverse");
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, playStoreUri);
        startActivity(playStoreIntent);
    }
}


}
