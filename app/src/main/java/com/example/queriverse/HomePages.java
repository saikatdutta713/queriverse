package com.example.queriverse;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queriverse.adapters.UserPostAdapter;
import com.example.queriverse.data.UserPost;

import java.util.ArrayList;
import java.util.List;

public class HomePages extends AppCompatActivity {

    private List<UserPost> userPostList = new ArrayList<>();
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_pages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_view_user_list);

        userPostList.add(new UserPost(
                R.drawable.profile_photo,
                "John Doe",
                "2024-04-13",
                "ByteLure is a website for tech blogs developed using WordPress. Showcased content creation, and community engagement the  skills. Produced impactful technical content and optimized SEO. Demonstrates a passion for tech trends and effective communication.",
                R.drawable.post_image,
                "10", // likes
                "2",  // dislikes
                "5"   // comments
        ));

        userPostList.add(new UserPost(
                R.drawable.profile_photo,
                "Jane Smith",
                "2024-04-12",
                "Another post example.",
                R.drawable.profile_photo,
                "20",
                "3",
                "8"
        ));

        userPostList.add(new UserPost(
                R.drawable.profile_photo,
                "Michael Johnson",
                "2024-04-11",
                "Check out this cool photo!",
                R.drawable.profile_photo,
                "15",
                "1",
                "12"
        ));

        userPostList.add(new UserPost(
                R.drawable.profile_photo,
                "Emily Brown",
                "2024-04-10",
                "Feeling grateful today. #blessed",
                R.drawable.profile_photo,
                "30",
                "0",
                "7"
        ));

        userPostList.add(new UserPost(
                R.drawable.profile_photo,
                "David Lee",
                "2024-04-09",
                "Excited for the weekend! ",
                R.drawable.post_image,
                "25",
                "5",
                "15"
        ));


        UserPostAdapter userPostAdapter = new UserPostAdapter(userPostList,this);
        recyclerView.setAdapter(userPostAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}