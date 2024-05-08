package com.example.queriverse;

import android.os.Bundle;
import android.util.Log;
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

        // Make API call
        makeApiCall(postId);
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
