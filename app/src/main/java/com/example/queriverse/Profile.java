package com.example.queriverse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.example.queriverse.data.ProfileSelf;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Profile extends AppCompatActivity {

    private TextView nameTextViewSelf, emailTextViewSelf, followerTextViewSelf, followingTextViewSelf, numberOfPostTextViewSelf, aboutTextViewSelf, updatedAbout;
    private ImageView profileImageViewSelf;

    private ImageButton editButtonAbout, backButton,settingsButton;
    private Button updateButton;
    private View aboutSelfEditBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize UI elements
        nameTextViewSelf = findViewById(R.id.userSelfProfileName);
        emailTextViewSelf = findViewById(R.id.userSelfProfileEmail);
        profileImageViewSelf = findViewById(R.id.userSelfProfilePic);
        followerTextViewSelf = findViewById(R.id.userSelfFollower);
        followingTextViewSelf = findViewById(R.id.profileSelfFollowing);
        numberOfPostTextViewSelf = findViewById(R.id.profileSelfPost);
        aboutTextViewSelf = findViewById(R.id.aboutSelf);
        editButtonAbout = findViewById(R.id.aboutSelfEdit);
        aboutSelfEditBox = findViewById(R.id.aboutSelfEditBox);
        updateButton = findViewById(R.id.aboutButtonSelfEditBox);
        updatedAbout = findViewById(R.id.aboutTextSelfEditBox);
        backButton = findViewById(R.id.backButtonSelf);
        settingsButton = findViewById(R.id.imageButtonSettings);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Set OnClickListener for edit button
        editButtonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of aboutSelfEditBox
                if (aboutSelfEditBox.getVisibility() == View.VISIBLE) {
                    // If aboutSelfEditBox is visible, hide it
                    aboutSelfEditBox.setVisibility(View.GONE);
                } else {
                    // If aboutSelfEditBox is hidden, show it
                    aboutSelfEditBox.setVisibility(View.VISIBLE);
                    // Make the TextView editable
                    aboutTextViewSelf.setFocusableInTouchMode(true);
                    aboutTextViewSelf.setFocusable(true);
                    aboutTextViewSelf.requestFocus();
                    // Set focus on the updatedAbout TextView
                    updatedAbout.requestFocus();
                    updatedAbout.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // Not needed for this implementation of 200 character
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Not needed for this implementation of 200 character
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Check if the length exceeds 200 characters
                            if (s.length() > 200) {
                                // If the length exceeds 200 characters, disable further input
                                updatedAbout.setEnabled(false);
                                // Display a message to the user
                                Toast.makeText(Profile.this, "Maximum character limit reached", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });;


        // Set OnClickListener for update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make API call to update about field
                updateAboutField(updatedAbout.getText().toString());
            }
        });



        // Make Api call
        makeApiCall();
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.layout.menu_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_logout) {
                    // Handle logout action
                    Toast.makeText(Profile.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }



    private void updateAboutField(String updatedAboutText) {
        // Construct the JSON object with the updated about text
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("about", updatedAboutText);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Abort if there's an error creating the JSON object
        }

        // Make a PUT request to update the about field
        String apiUrl = "https://queriverse.bytelure.in/api/users/3"; // Update the user ID if needed

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, apiUrl, requestData,
                response -> {
                    // Handle successful response
                    Toast.makeText(Profile.this, "About field updated successfully", Toast.LENGTH_SHORT).show();
                    aboutSelfEditBox.setVisibility(View.GONE); // Hide the aboutSelfEditBox

                    // Update the about TextView directly
                    aboutTextViewSelf.setText(updatedAboutText);
                },
                error -> {
                    // Handle error response
                    Log.e("UpdateAboutError", "Error updating about field: " + error.toString());
                    Toast.makeText(Profile.this, "Error updating about field", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }





    private void makeApiCall() {
        String apiUrl = "https://queriverse.bytelure.in/api/users/3";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,

                (response) -> {
                    try {
                        // Parse JSON response and create ProfileSelf object
                        ProfileSelf profile = parseJsonResponse(response);

                        // Display profile data in UI
                        displayProfile(profile);

                    } catch (JSONException e) {
                        Log.e("JSONParsingError", "Error parsing JSON response: " + e.getMessage(), e);
                        Toast.makeText(Profile.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                    }
                },
                error ->  {
                    Log.e("VolleyError", "Error fetching data from server", error);
                    Toast.makeText(Profile.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(jsonObjectRequest);
    }


    private ProfileSelf parseJsonResponse(JSONObject response) throws JSONException {
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

        return new ProfileSelf(name, email, profileImage, follower, following, numberOfPost, about);
    }

    private void displayProfile (ProfileSelf profile) {
        nameTextViewSelf.setText(profile.getNameSelf());
        emailTextViewSelf.setText(profile.getEmailSelf());
        Picasso.get().load(profile.getProfileImageSelf()).into(profileImageViewSelf);
        followerTextViewSelf.setText(profile.getFollowerSelf());
        followingTextViewSelf.setText(profile.getFollowingSelf());
        numberOfPostTextViewSelf.setText(profile.getNumberOfPostSelf());
        aboutTextViewSelf.setText(profile.getAboutSelf());
    }
}