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
import com.example.queriverse.data.UserComment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserCommentAdapter extends RecyclerView.Adapter<UserCommentAdapter.UserCommentViewHolder> {
    private List<UserComment> userCommentList;
    private Context context;

    public UserCommentAdapter(List<UserComment> userCommentList, Context context) {
        this.userCommentList = userCommentList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_view, parent, false);
        return new UserCommentAdapter.UserCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCommentViewHolder holder, int position) {
        UserComment userComment = userCommentList.get(position);
        // Load author image if the URL is provided
        if(userComment.getCommentauthorImageUrl() != null && !userComment.getCommentauthorImageUrl().isEmpty()) {
            Picasso.get().load(userComment.getCommentauthorImageUrl()).into(holder.commentAuthorImageView);
        } else {
            // Hide author image view if URL is not provided
            holder.commentAuthorImageView.setVisibility(View.GONE);
        }
        holder.commentNameView.setText(userComment.getCommenttauthorName());
        holder.commentDateView.setText(userComment.getCommentDate());
        holder.commentDescriptionView.setText(userComment.getCommentDescription());
        holder.commentLikesView.setText(userComment.getCommentLikes());
        holder.commentDislikesView.setText(userComment.getCommentDislikes());
    }


    @Override
    public int getItemCount() {
        return userCommentList.size();
    }

    static class UserCommentViewHolder extends RecyclerView.ViewHolder{
        private ImageView commentAuthorImageView;
        private TextView commentNameView,commentDateView,commentDescriptionView,commentLikesView,commentDislikesView;

        public UserCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentAuthorImageView = itemView.findViewById(R.id.idCommentCVAuthor);
            commentNameView = itemView.findViewById(R.id.idCommentAuthorName);
            commentDateView = itemView.findViewById(R.id.idCommentTime);
            commentDescriptionView = itemView.findViewById(R.id.idCommentDescription);
            commentLikesView = itemView.findViewById(R.id.idCommentLikes);
            commentDislikesView = itemView.findViewById(R.id.idCommentDislikes);
        }
    }
}
