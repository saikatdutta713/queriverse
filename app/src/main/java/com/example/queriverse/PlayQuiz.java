package com.example.queriverse;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.queriverse.databinding.ActivityPlayQuizBinding;

public class PlayQuiz extends AppCompatActivity implements View.OnClickListener {

    ActivityPlayQuizBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityPlayQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponent();

    }

    private void initComponent() {
        binding.option1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "button clicked", Toast.LENGTH_SHORT).show();
    }
}