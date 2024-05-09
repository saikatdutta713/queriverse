//UserPostAdapter.java

package com.example.queriverse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queriverse.OtherUserProfile;
import com.example.queriverse.R;
import com.example.queriverse.SinglePost;
import com.example.queriverse.data.UserPost;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>
{

    private final List<UserPost> userPostList;
    private final Context context;
    private final String postId; // Add post ID field
    private final String loggedinUserId = "5"; // Set the logged in user ID to 5

    public UserPostAdapter(List<UserPost> userPostList, Context context, String postId) {
        this.userPostList = userPostList;
        this.context = context;
        this.postId = postId; // Initialize post ID
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // xml view --> java object
        View view = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        UserPost userPost = userPostList.get(position);

        // Set click listener for the share button
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the sharePost method and pass the UserPost object
                sharePost(userPost);
            }
        });

        // Set click listeners on post
        holder.postRelativeLayout.setOnClickListener(view -> {
            // Redirect to SinglePostActivity with post ID
            Intent intent = new Intent(context, SinglePost.class);
            intent.putExtra("postId", userPost.getPostId());
            context.startActivity(intent);
        });

        // Set click listener for the author image view
        holder.authorImageView.setOnClickListener(v -> {
            // Open OtherUserProfile activity and pass user ID
            Intent intent = new Intent(context, OtherUserProfile.class);
            intent.putExtra("userId", userPost.getUserId());
            context.startActivity(intent);
        });


        // Set click listener for the author name view
        holder.nameView.setOnClickListener(v -> {
            // Open OtherUserProfile activity and pass user ID
            Intent intent = new Intent(context, OtherUserProfile.class);
            intent.putExtra("userId", userPost.getUserId());
            context.startActivity(intent);
        });


        // Load author image if the URL is provided
        if(userPost.getAuthorImageUrl() != null && !userPost.getAuthorImageUrl().isEmpty()) {
            Picasso.get().load(userPost.getAuthorImageUrl()).into(holder.authorImageView);
        } else {
            // Hide author image view if URL is not provided
            holder.authorImageView.setVisibility(View.GONE);
        }

        // Load post image if the URL is provided
        if (userPost.getPostImageUrl() != null && !userPost.getPostImageUrl().isEmpty()) {
            Picasso.get().load(userPost.getPostImageUrl()).into(holder.ivView);
        } else {
            // Hide post image view if URL is not provided
            holder.ivView.setVisibility(View.GONE);
        }

        // holder.authorImageView.setImageAlpha(userPost.getAuthorImage());
        holder.nameView.setText(userPost.getAuthorName());
        holder.dateView.setText(userPost.getPostDate());
        holder.descriptionView.setText(userPost.getPostDescription());
        holder.likesView.setText(userPost.getPostLikes());
        holder.dislikesView.setText(userPost.getPostDislikes());
        holder.commentsView.setText(userPost.getPostComments());

        // Set click listener for the like button
        holder.likeImage.setOnClickListener(v -> likePost(userPost));

        holder.dislikeImage.setOnClickListener(v -> dislikePost(userPost));
    }

    @Override
    public int getItemCount() {
        return userPostList.size();
    }

    static class UserPostViewHolder extends RecyclerView.ViewHolder{

        private final ImageView authorImageView,ivView,likeImage,dislikeImage;
        private final TextView nameView,dateView,descriptionView,likesView,dislikesView,commentsView;
        private ImageButton shareButton;
        private RelativeLayout postRelativeLayout;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorImageView = itemView.findViewById(R.id.idCVAuthor);
            ivView = itemView.findViewById(R.id.idIVPost);
            nameView = itemView.findViewById(R.id.idTVAuthorName);
            dateView = itemView.findViewById(R.id.idTVTime);
            descriptionView = itemView.findViewById(R.id.idTVDescription);
            likesView = itemView.findViewById(R.id.idTVLikes);
            dislikesView = itemView.findViewById(R.id.idTVDislikes);
            commentsView = itemView.findViewById(R.id.idTVComments);
            shareButton = itemView.findViewById(R.id.idTVShare);
            postRelativeLayout = itemView.findViewById(R.id.postRelativeLayout);
            likeImage = itemView.findViewById(R.id.likeImage);
            dislikeImage = itemView.findViewById(R.id.dislikeImage);

        }
    }

    private void sharePost(UserPost userPost) {
        // Create a sharing Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String shareMessage = userPost.getPostDescription();
        String postLink = userPost.generatePostLink();
        if (postLink != null && !postLink.isEmpty()) {
            shareMessage += "\n" + postLink; // Add the link to the share message
        }

        // Set the share message
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        // Show the system's share chooser
        context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }

    // Method to handle post like action
    @SuppressLint("StaticFieldLeak")
    private void likePost(UserPost userPost) {
        // Implement the logic to like the post
        String likeEndpoint = "https://queriverse.bytelure.in/api/posts/" + userPost.getPostId() + "/like";

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    // Create JSON object with user ID
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("user", loggedinUserId); // Assuming loggedinUserId is the user ID

                    // Make the API call to like the post with user ID in the request body
                    URL url = new URL(likeEndpoint);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(requestBody.toString().getBytes("UTF-8"));
                    outputStream.close();

                    int responseCode = urlConnection.getResponseCode();
                    // Return the updated likes count from the server response
                    return responseCode == HttpURLConnection.HTTP_OK ? Integer.parseInt(userPost.getPostLikes()) + 1 : -1;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer updatedLikesCount) {
                super.onPostExecute(updatedLikesCount);
                if (updatedLikesCount >= 0) {
                    // Update the likes count in the UserPost object
                    int currentLikes = Integer.parseInt(userPost.getPostLikes());
                    userPost.setPostLikes(String.valueOf(currentLikes - 1));
                    // Mark the post as liked
                    userPost.setLikedByUser(true);
                    // Notify the adapter to update the UI
                    notifyDataSetChanged();
                    // Show success message
                    Toast.makeText(context, "Post Like Removed", Toast.LENGTH_SHORT).show();
                } else {
                    // Show failure message
                    Toast.makeText(context, "Post Liked", Toast.LENGTH_SHORT).show();
                    // Update the likes count in the UserPost object
                    int currentLikes = Integer.parseInt(userPost.getPostLikes());
                    userPost.setPostLikes(String.valueOf(currentLikes + 1)); // Increment like count
                    // Notify the adapter to update the UI
                    notifyDataSetChanged();
                }
            }



        }.execute();
    }


    // Method to handle post dislike action
    @SuppressLint("StaticFieldLeak")
    private void dislikePost(UserPost userPost) {
        // Implement the logic to like the post
        String dislikeEndpoint = "https://queriverse.bytelure.in/api/posts/" + userPost.getPostId() + "/dislike";

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    // Create JSON object with user ID
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("user", loggedinUserId); // Assuming loggedinUserId is the user ID

                    // Make the API call to dislike the post with user ID in the request body
                    URL url = new URL(dislikeEndpoint);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(requestBody.toString().getBytes("UTF-8"));
                    outputStream.close();

                    int responseCode = urlConnection.getResponseCode();
                    // Return the updated dislikes count from the server response
                    return responseCode == HttpURLConnection.HTTP_OK ? Integer.parseInt(userPost.getPostDislikes()) + 1 : -1;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer updatedDisikesCount) {
                super.onPostExecute(updatedDisikesCount);
                if (updatedDisikesCount >= 0) {
                    // Update the likes count in the UserPost object
                    int currentDislikes = Integer.parseInt(userPost.getPostDislikes());
                    userPost.setPostDislikes(String.valueOf(currentDislikes - 1));
                    // Mark the post as liked
                    userPost.setDislikedByUser(true);
                    // Notify the adapter to update the UI
                    notifyDataSetChanged();
                    // Show success message
                    Toast.makeText(context, "Post Dislike Removed", Toast.LENGTH_SHORT).show();
                } else {
                    // Show failure message
                    Toast.makeText(context, "Post Disliked", Toast.LENGTH_SHORT).show();
                    // Update the likes count in the UserPost object
                    int currentDislikes = Integer.parseInt(userPost.getPostDislikes());
                    userPost.setPostDislikes(String.valueOf(currentDislikes + 1)); // Increment like count
                    // Notify the adapter to update the UI
                    notifyDataSetChanged();
                }
            }



        }.execute();
    }


}

