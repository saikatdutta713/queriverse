package com.example.queriverse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.queriverse.data.ProfileOtherUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OtherUserProfile extends AppCompatActivity {


    private TextView nameTextView, emailTextView, followerTextView, followingTextView, numberOfPostTextView, aboutTextView;
    private ImageView profileImageView;
    private Button followButton;
    private final String loggedinUserId = "5"; // Set the logged in user ID to 5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other_user_profile);

        // Get user ID from Intent
        String userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        nameTextView = findViewById(R.id.userProfileName);
        emailTextView = findViewById(R.id.userProfileEmail);
        profileImageView = findViewById(R.id.userProfilePic);
        followerTextView = findViewById(R.id.profileFollower);
        followingTextView = findViewById(R.id.profileFollowing);
        numberOfPostTextView = findViewById(R.id.profilePost);
        aboutTextView = findViewById(R.id.about);
        followButton = findViewById(R.id.userProfileFollow);

        // Set OnClickListener for the back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set OnClickListener for the follow button
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiUrl = "https://queriverse.bytelure.in/api/users/" + loggedinUserId + "/follow/" + userId;

                RequestQueue requestQueue = Volley.newRequestQueue(OtherUserProfile.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    // Check if the "follow" key is present in the JSON response
                                    if (response.has("follow")) {
                                        // Retrieve the boolean value of the "follow" key
                                        boolean isFollowed = response.getBoolean("follow");

                                        // Update followers count in UI based on follow/unfollow action
                                        int currentFollowersCount = Integer.parseInt(followerTextView.getText().toString());
                                        if (isFollowed) {
                                            // User is followed successfully
                                            Toast.makeText(OtherUserProfile.this, "You are now following this user", Toast.LENGTH_SHORT).show();
                                            // Increment followers count by 1
                                            followerTextView.setText(String.valueOf(currentFollowersCount + 1));
                                        } else {
                                            // User is unfollowed successfully
                                            Toast.makeText(OtherUserProfile.this, "You have unfollowed this user", Toast.LENGTH_SHORT).show();
                                            // Decrement followers count by 1 (ensure count doesn't go below 0)
                                            followerTextView.setText(String.valueOf(Math.max(0, currentFollowersCount - 1)));
                                        }
                                    } else {
                                        // Handle the case where the "follow" key is missing
                                        Log.e("JSONParsingError", "No value for 'follow' in JSON response");
                                        Toast.makeText(OtherUserProfile.this, "No value for 'follow' in JSON response", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    // JSON parsing error
                                    Log.e("JSONParsingError", "Error parsing JSON response: " + e.getMessage(), e);
                                    Toast.makeText(OtherUserProfile.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VolleyError", "Error making follow/unfollow request", error);
                                Toast.makeText(OtherUserProfile.this, "Error making follow/unfollow request", Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(jsonObjectRequest);
            }
        });











        // Make Api call
        makeApiCall(userId);
    }


    private void makeApiCall(String userId) {
        String apiUrl = "https://queriverse.bytelure.in/api/users/" + userId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,

                (response) -> {
                        try {
                            // Parse JSON response and create ProfileOtherUser object
                            ProfileOtherUser profile = parseJsonResponse(response);

                            // Display profile data in UI
                            displayProfile(profile);

                        } catch (JSONException e) {
                            Log.e("JSONParsingError", "Error parsing JSON response: " + e.getMessage(), e);
                            Toast.makeText(OtherUserProfile.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                },
                error ->  {
                    Log.e("VolleyError", "Error fetching data from server", error);
                    Toast.makeText(OtherUserProfile.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
    }

    private ProfileOtherUser parseJsonResponse(JSONObject response) throws JSONException {
        String name = response.getString("username");
        String email = response.getString("email");
        String profileImage = response.getString("picture");

        // Parse followers and following arrays
        JSONArray followersArray = response.optJSONArray("followers");
        JSONArray followingArray = response.optJSONArray("following");

        // Get lengths of followers and following arrays
        int followerCount = (followersArray != null) ? followersArray.length() : 0;
        int followingCount = (followingArray != null) ? followingArray.length() : 0;

        String follower = String.valueOf(followerCount);
        String following = String.valueOf(followingCount);
        String numberOfPost = response.optString("number_of_post", "0");
        String about = response.optString("about", "");

        return new ProfileOtherUser(name, email, profileImage, follower, following, numberOfPost, about);
    }

    private void displayProfile (ProfileOtherUser profile) {
        nameTextView.setText(profile.getName());
        emailTextView.setText(profile.getEmail());
        Picasso.get().load(profile.getProfileImage()).into(profileImageView);
        followerTextView.setText(profile.getFollower());
        followingTextView.setText(profile.getFollowing());
        numberOfPostTextView.setText(profile.getNumberOfPost());
        aboutTextView.setText(profile.getAbout());
    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(OtherUserProfile.this, HomePages.class);
        startActivity(intent);
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(OtherUserProfile.this, CreatePost.class);
        startActivity(intent);
    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(OtherUserProfile.this, Profile.class);
        startActivity(intent);

    }

    public void playQuiz(View view) {
        Intent intent = new Intent(OtherUserProfile.this, QuizCategory.class);
        startActivity(intent);
    }
}