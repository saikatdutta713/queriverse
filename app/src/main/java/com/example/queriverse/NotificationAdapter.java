package com.example.queriverse;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queriverse.R;
import com.example.queriverse.SingleNotification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<SingleNotification> notifications;

    public NotificationAdapter(List<SingleNotification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        SingleNotification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;
        private final TextView textViewContent;
        private final ImageView imageViewNotification;

        @SuppressLint("ResourceType")
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewNotification = itemView.findViewById(R.id.imageViewIcon);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }

        public void bind(SingleNotification notification) {
            switch (notification.getType()) {
                case "follow":
                    imageViewNotification.setImageResource(R.drawable.ic_followers_notification);
                    break;
                case "message":
                    imageViewNotification.setImageResource(R.drawable.ic_5bars);
                    break;
                case "like":
                    imageViewNotification.setImageResource(R.drawable.ic_camera);
                    break;
                case "event":
                    imageViewNotification.setImageResource(R.drawable.ic_article);
                    break;
                default:
                    imageViewNotification.setImageResource(R.drawable.ic_a_lock);
                    break;
            }
            textViewTitle.setText(notification.getTitle());
            textViewContent.setText(notification.getContent());
        }
    }
}
