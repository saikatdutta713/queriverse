package com.example.queriverse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.queriverse.adapters.UserCommentAdapter;
import com.example.queriverse.data.UserComment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SinglePost extends AppCompatActivity {

    private final List<UserComment> userCommentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private UserCommentAdapter userCommentAdapter;

    private final String loggedinUserId = "6";
    private ImageView authorImageView, ivView;
    private TextView nameView, dateView, descriptionView, likesView, dislikesView, commentsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        // Get post ID from Intent
        String postId = getIntent().getStringExtra("postId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the ImageView for liking the post
        ImageView likeImageView = findViewById(R.id.imageLike);
        likeImageView.setOnClickListener(v -> likePost(postId));

        // Find the ImageView for disliking the post
        ImageView dislikeImageView = findViewById(R.id.imageDislike);
        dislikeImageView.setOnClickListener(v -> dislikePost(postId));

        recyclerView = findViewById(R.id.recycler_view_comment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestQueue = Volley.newRequestQueue(this);

        authorImageView = findViewById(R.id.idCVAuthor);
        ivView = findViewById(R.id.idIVPost);
        nameView = findViewById(R.id.idTVAuthorName);
        dateView = findViewById(R.id.idTVTime);
        descriptionView = findViewById(R.id.idTVDescription);
        likesView = findViewById(R.id.idTVLikes);
        dislikesView = findViewById(R.id.idTVDislikes);
        commentsView = findViewById(R.id.idTVComments);

        userCommentAdapter = new UserCommentAdapter(userCommentList, this);
        recyclerView.setAdapter(userCommentAdapter);

        EditText commentEditText = findViewById(R.id.edit_comment);
        ImageButton submitButton = findViewById(R.id.btn_submitComment);

// Set click listener on the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the comment text from the EditText
                String commentText = commentEditText.getText().toString().trim();

                // Check if the comment is not empty
                if (!commentText.isEmpty()) {
                    // Make API call to add the comment
                    addCommentToPost(postId, commentText);
                    // Clear the EditText
                    commentEditText.setText("");
                    commentEditText.clearFocus();
                } else {
                    // Show a message to enter a comment
                    Toast.makeText(SinglePost.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Make API call
        makeApiCall(postId);
    }


    private void addCommentToPost(String postId, String commentText) {
        String apiUrl = "https://queriverse.bytelure.in/api/posts/" + postId + "/comment";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user", loggedinUserId);
            requestBody.put("text", commentText);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            JSONObject commentData = response.getJSONObject("comment");
                            if (commentData != null) {
                                // Increment the comment count in the UI
                                int currentCommentCount = userCommentList.size();
                                commentsView.setText(String.valueOf(currentCommentCount + 1));

                                // Parse and display the new comment
                                String userId = commentData.getString("user");
                                String commentDate = commentData.optString("created_at");
                                String commentDescription = commentData.optString("text");
                                String commentLikes = commentData.optString("likes");
                                String commentDislikes = commentData.optString("dislikes");

                                fetchUserData(userId, new UserDataCallback() {
                                    @Override
                                    public void onSuccess(String username, String profileImage) {
                                        UserComment userComment = new UserComment(userId, profileImage, username, commentDate, commentDescription, commentLikes, commentDislikes);
                                        userCommentList.add(userComment);
                                        userCommentAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {
                                        error.printStackTrace();
                                        Toast.makeText(SinglePost.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Toast.makeText(SinglePost.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SinglePost.this, "Error parsing comment JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error adding comment", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    // Method to handle liking a post
    private void likePost(String postId) {
        String apiUrl = "https://queriverse.bytelure.in/api/posts/" + postId + "/like";

        JSONObject requestBody = new JSONObject();
        try {
            // Add loggedinUserId to the request body
            requestBody.put("user", loggedinUserId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean likeSuccess = response.getBoolean("like");
                            String message = response.getString("message");
                            JSONArray likesArray = response.getJSONArray("likes");

                            if (likeSuccess) {
                                // Like operation was successful
                                Toast.makeText(SinglePost.this, message, Toast.LENGTH_SHORT).show();

                                // Update UI with the updated likes count
                                int newLikesCount = likesArray.length(); // Length of the likes array
                                likesView.setText(String.valueOf(newLikesCount)); // Update likes count TextView
                            } else {
                                // Like operation failed
                                Toast.makeText(SinglePost.this, message, Toast.LENGTH_SHORT).show();
                                int newLikesCount = likesArray.length(); // Length of the likes array
                                likesView.setText(String.valueOf(newLikesCount));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SinglePost.this, "Error parsing like JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error liking post", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private void dislikePost(String postId) {
        String apiUrl = "https://queriverse.bytelure.in/api/posts/" + postId + "/dislike";

        JSONObject requestBody = new JSONObject();
        try {
            // Add loggedinUserId to the request body
            requestBody.put("user", loggedinUserId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");

                            // Check if dislikes array is present in the response
                            if (response.has("dislikes")) {
                                JSONArray dislikesArray = response.getJSONArray("dislikes");
                                // Update UI with the updated dislikes count
                                int newDislikesCount = dislikesArray.length(); // Length of the dislikes array
                                dislikesView.setText(String.valueOf(newDislikesCount)); // Update dislikes count TextView
                            } else {
                                // Handle case where dislikes array is missing in response
                                Log.e("DislikeResponseError", "No 'dislikes' array in JSON response");
                            }

                            // Display the message in a Toast
                            Toast.makeText(SinglePost.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SinglePost.this, "Error parsing dislike JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error disliking post", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }




    private void makeApiCall(String postId) {
        String apiUrl = "https://queriverse.bytelure.in/api/posts/" + postId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse JSON response and display post details
                        displayPostDetails(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error fetching data from server", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void displayPostDetails(JSONObject post) {
        try {
            String userId = post.optString("user");
            String date = post.optString("created_at");
            String description = post.optString("text");
            JSONArray likesArray = post.optJSONArray("likes");
            JSONArray dislikesArray = post.optJSONArray("dislikes");
            JSONArray commentsArray = post.optJSONArray("comments");

            int likesCount = likesArray != null ? likesArray.length() : 0;
            int dislikesCount = dislikesArray != null ? dislikesArray.length() : 0;
            int commentCount = commentsArray != null ? commentsArray.length() : 0;

            String likes = String.valueOf(likesCount);
            String dislikes = String.valueOf(dislikesCount);
            String comments = String.valueOf(commentCount);

            fetchUserDetails(userId);

            dateView.setText(date);
            descriptionView.setText(description);
            likesView.setText(likes);
            dislikesView.setText(dislikes);
            commentsView.setText(comments);

            String postImageUrl = post.optString("image");
            if (!postImageUrl.isEmpty()) {
                Picasso.get().load(postImageUrl).into(ivView);
            }

            if (commentsArray != null) {
                for (int i = 0; i < commentsArray.length(); i++) {
                    int commentId = commentsArray.getInt(i);
                    fetchCommentDetails(commentId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SinglePost.this, "Error parsing post JSON response", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCommentDetails(int commentId) {
        String commentApiUrl = "https://queriverse.bytelure.in/api/comments/" + commentId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, commentApiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            parseCommentResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SinglePost.this, "Error parsing comment JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error fetching comment data from server", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void parseCommentResponse(JSONObject response) throws JSONException {
        String userId = response.getString("user");
        String commentDate = response.optString("created_at");
        String commentDescription = response.optString("text");
        String commentLikes = response.optString("likes");
        String commentDislikes = response.optString("dislikes");

        fetchUserData(userId, new UserDataCallback() {
            @Override
            public void onSuccess(String username, String profileImage) {
                UserComment userComment = new UserComment(userId, profileImage, username, commentDate, commentDescription, commentLikes, commentDislikes);
                userCommentList.add(userComment);
                userCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(SinglePost.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private interface UserDataCallback {
        void onSuccess(String username, String authorImage);

        void onError(VolleyError error);
    }

    private void fetchUserDetails(String userId) {
        String userApiUrl = "https://queriverse.bytelure.in/api/users/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, userApiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String username = response.optString("username");
                            String userImageUrl = response.optString("picture");
                            nameView.setText(username);
                            Picasso.get().load(userImageUrl).into(authorImageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SinglePost.this, "Error parsing user JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SinglePost.this, "Error fetching user data from server", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
