package com.example.queriverse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queriverse.R;
import com.example.queriverse.data.UserPost;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>
{

    private List<UserPost> userPostList;
    private Context context;

    public UserPostAdapter(List<UserPost> userPostList, Context context) {
        this.userPostList = userPostList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // xml view --> java object
        View view = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        UserPostViewHolder userPostViewHolder = new UserPostViewHolder(view);
        return userPostViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        UserPost userPost = userPostList.get(position);
        holder.authorImageView.setImageAlpha(userPost.getAuthorImage());
        holder.ivView.setImageAlpha(userPost.getPostIV());
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

    class UserPostViewHolder extends RecyclerView.ViewHolder{

        private ImageView authorImageView,ivView;
        private TextView nameView,dateView,descriptionView,likesView,dislikesView,commentsView;

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

        }
    }

}
