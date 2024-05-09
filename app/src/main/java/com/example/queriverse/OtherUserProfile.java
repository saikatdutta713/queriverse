package com.example.queriverse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        // Set OnClickListener for the back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}