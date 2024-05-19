package com.example.queriverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizCategory extends AppCompatActivity {
    private static final String TAG = "QuizCategory log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void ChooseCategory(View view) {

        String value = (String) view.getTag();

        Intent intent = new Intent(this, PlayQuiz.class);
        intent.putExtra("category", value);
        startActivity(intent);

    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(QuizCategory.this, HomePages.class);
        startActivity(intent);
    }

    public void playQuiz(View view) {
        Intent intent = new Intent(QuizCategory.this, QuizCategory.class);
        startActivity(intent);
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(QuizCategory.this, CreatePost.class);
        startActivity(intent);
    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(QuizCategory.this, Profile.class);
        startActivity(intent);
    }
}