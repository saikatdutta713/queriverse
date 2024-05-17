package com.example.queriverse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.queriverse.data.Question;
import com.example.queriverse.databinding.ActivityQuizResultBinding;

import java.util.ArrayList;

public class QuizResult extends AppCompatActivity {

    ActivityQuizResultBinding binding;
    private ArrayList<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        questions = getIntent().getParcelableArrayListExtra("questions");
        displayResults();
    }

    private void displayResults() {
        if (questions != null && !questions.isEmpty()) {
            int totalScore = 0;
            for (Question question : questions) {
                addQuestionView(question);
                // Get the given answer option
                String givenAnswer = question.getGivenAnswer().toUpperCase(); // Convert to uppercase
                // Get the correct answer option
                String correctAnswer = question.getAnswer().toUpperCase(); // Convert to uppercase
                // Check if the given answer matches the correct answer
                if (givenAnswer.equals(correctAnswer)) {
                    totalScore++;
                }

                // Log the details for each question
                Log.d("QuestionDetails", "Question: " + question.getQuestion());
                Log.d("QuestionDetails", "Given Answer: " + givenAnswer);
                Log.d("QuestionDetails", "Correct Answer: " + correctAnswer);
                Log.d("QuestionDetails", "Total Score: " + totalScore);
            }
            binding.textTotalScore.setText("Total Score: " + totalScore + " out of " + questions.size());
        }
    }








    private void addQuestionView(Question question) {
        LinearLayout questionLayout = new LinearLayout(this);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.setPadding(16, 16, 16, 16);

        TextView textQuestion = new TextView(this);
        textQuestion.setText(question.getQuestion());
        textQuestion.setTypeface(null, Typeface.BOLD); // Set bold
        textQuestion.setTextColor(ContextCompat.getColor(this, R.color.secondary)); // Change color
        textQuestion.setTextSize(18); // Set text size in SP
        questionLayout.addView(textQuestion);

        TextView textOptions = new TextView(this);
        textOptions.setText("Options: ");
        questionLayout.addView(textOptions);

        TextView textOptionA = new TextView(this);
        textOptionA.setText("A) " + question.getOption1());
        questionLayout.addView(textOptionA);

        TextView textOptionB = new TextView(this);
        textOptionB.setText("B) " + question.getOption2());
        questionLayout.addView(textOptionB);

        TextView textOptionC = new TextView(this);
        textOptionC.setText("C) " + question.getOption3());
        questionLayout.addView(textOptionC);

        TextView textOptionD = new TextView(this);
        textOptionD.setText("D) " + question.getOption4());
        questionLayout.addView(textOptionD);

        TextView textChosenOption = new TextView(this);
        textChosenOption.setText("Chosen Option: " + question.getGivenAnswer());
        questionLayout.addView(textChosenOption);

        TextView textCorrectOption = new TextView(this);
        String correctOption = question.getAnswer().toUpperCase(); // Convert to uppercase
        textCorrectOption.setText("Correct Option: " + correctOption);
        textCorrectOption.setTypeface(null, Typeface.BOLD); // Set bold
        textCorrectOption.setTextColor(ContextCompat.getColor(this, R.color.accent)); // Change color
        questionLayout.addView(textCorrectOption);


        binding.questionContainer.addView(questionLayout);
    }

    public void onHomeClick(View view) {
        Intent intent = new Intent(QuizResult.this, HomePages.class);
        startActivity(intent);
    }

    public void onPostClick(View view) {
        Intent intent = new Intent(QuizResult.this, HomePages.class);
        startActivity(intent);
    }

    public void onProfileClick(View view) {
        Intent intent = new Intent(QuizResult.this, HomePages.class);
        startActivity(intent);
    }
}
