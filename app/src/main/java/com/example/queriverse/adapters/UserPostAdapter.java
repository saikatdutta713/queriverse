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
    private final int loggedinUserId = 5; // Set the logged in user ID to 5

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

    }

    @Override
    public int getItemCount() {
        return userPostList.size();
    }

    static class UserPostViewHolder extends RecyclerView.ViewHolder{

        private final ImageView authorImageView,ivView,likeImage;
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










}

