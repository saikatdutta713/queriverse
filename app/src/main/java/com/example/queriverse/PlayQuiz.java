    package com.example.queriverse;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;
    import android.os.CountDownTimer;
    import android.util.Log;
    import android.view.View;
    import android.widget.RadioButton;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.android.volley.RequestQueue;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.queriverse.config.Constants;
    import com.example.queriverse.data.Question;
    import com.example.queriverse.databinding.ActivityPlayQuizBinding;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.Response;

    public class PlayQuiz extends AppCompatActivity implements View.OnClickListener {

        private static final String TAG = "PlayQuiz log";

        ActivityPlayQuizBinding binding;
        private RadioButton checkButton[] = new RadioButton[4];
        int currentIndex = 0;
        List<Question> questions;
        private CountDownTimer timer;
        private int timeLeft = Constants.TOTAL_EXAM_TIME;

        String category;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            binding = ActivityPlayQuizBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            category = getIntent().getStringExtra("category");

            Log.i(TAG, "onCreate: "+category);

            initComponent();
            startTimer();
        }

        private void submitTest() {
            Intent intent = new Intent(PlayQuiz.this, QuizResult.class);
            intent.putParcelableArrayListExtra("questions", (ArrayList<Question>) questions);
            startActivity(intent);
            finish();
        }

        // Start timer method
        private void startTimer() {
            timer = new CountDownTimer(Constants.TOTAL_EXAM_TIME * 1000, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    int min = timeLeft / 60;
                    int sec = timeLeft % 60;
                    binding.quizTimmer.setText("Timer: " + min + " min " + sec + " sec ");
                    timeLeft--;

                    //update circular progress bar
    //                binding.circularProgressBar.setProgress((float) timeLeft / Constants.TOTAL_EXAM_TIME * 100);
                }

                @Override
                public void onFinish() {
                    submitTest();
                    binding.quizTimmer.setText("Finished");
                }
            };
            timer.start();
        }

        private void fetchQuestions() {
            String url = "https://trivianuts.bytelure.in/api/quizzes/category/"+category.trim()+"/today";
    //        RequestQueue requestQueue = Volley.newRequestQueue(this);
    //        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
    //                Request.Method.GET,
    //                url,
    //                null,
    //                new Response.Listener<JSONArray>() {
    //                    @Override
    //                    public void onResponse(JSONArray response) {
    //                        questions = new ArrayList<>();
    //                        Log.i(TAG, "onResponse: "+questions);
    //                        try {
    //                            for (int i = 0; i < response.length(); i++) {
    //                                JSONObject jsonObject = response.getJSONObject(i);
    //                                Question question = new Question(
    //                                        jsonObject.getInt("question_id"),
    //                                        jsonObject.getString("question_text"),
    //                                        jsonObject.getString("answer_option_a"),
    //                                        jsonObject.getString("answer_option_b"),
    //                                        jsonObject.getString("answer_option_c"),
    //                                        jsonObject.getString("answer_option_d"),
    //                                        jsonObject.getString("correct_option")
    //                                );
    //                                questions.add(question);
    //                            }
    //                            // Display the first question
    //                            if (questions != null && !questions.isEmpty()) {
    //                                setQuestionToView(questions.get(0), 0);
    //                            } else {
    //                                Toast.makeText(PlayQuiz.this, "No questions available", Toast.LENGTH_SHORT).show();
    //                            }
    //
    //                        } catch (JSONException e) {
    //                            e.printStackTrace();
    //                            Toast.makeText(PlayQuiz.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
    //                        }
    //                    }
    //                },
    //                new Response.ErrorListener() {
    //                    @Override
    //                    public void onErrorResponse(VolleyError error) {
    //                        Toast.makeText(PlayQuiz.this, "Error fetching data", Toast.LENGTH_SHORT).show();
    //                    }
    //                }
    //        );

    //        requestQueue.add(jsonArrayRequest);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onFailure: "+e.getMessage() );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PlayQuiz.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlayQuiz.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    try {
                        JSONArray responseData = new JSONArray(response.body().string());
                        Log.i(TAG, "onResponse: "+responseData );
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject jsonObject = responseData.getJSONObject(i);
                            Question question = new Question(
                                    jsonObject.getInt("question_id"),
                                    jsonObject.getString("question_text"),
                                    jsonObject.getString("answer_option_a"),
                                    jsonObject.getString("answer_option_b"),
                                    jsonObject.getString("answer_option_c"),
                                    jsonObject.getString("answer_option_d"),
                                    jsonObject.getString("correct_option")
                            );
                            questions.add(question);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onResponseJsonException: "+e.getMessage() );
                        throw new RuntimeException(e);
                    }

                }
            });
        }

        private void initComponent() {
            checkButton[0] = binding.option1;
            checkButton[1] = binding.option2;
            checkButton[2] = binding.option3;
            checkButton[3] = binding.option4;

            for (RadioButton button : checkButton) {
                button.setOnClickListener(this);
            }

            fetchQuestions();

            binding.previousButton.setOnClickListener(e -> previousQuestion());

            binding.nextButton.setOnClickListener(e -> nextQuestion());

            binding.quizSubmitButton.setOnClickListener(e -> submitTest());
        }

        private void previousQuestion() {
            if (currentIndex - 1 < 0) {
                Toast.makeText(this, "Already at 0th position", Toast.LENGTH_SHORT).show();
            } else {
                currentIndex = currentIndex - 1;
                setQuestionToView(questions.get(currentIndex), currentIndex);
            }
        }

        private void nextQuestion() {
            if (currentIndex + 1 > questions.size() - 1) {
                Toast.makeText(this, "Already at last question", Toast.LENGTH_SHORT).show();
            } else {
                currentIndex = currentIndex + 1;
                setQuestionToView(questions.get(currentIndex), currentIndex);
            }
        }

        @Override
        public void onClick(View v) {
            RadioButton buttonClicked = (RadioButton) v;

            // Log the ID of the clicked RadioButton
            Log.d(TAG, "ID: " + buttonClicked.getId());

            // Set the checked state of all RadioButtons
            for (RadioButton button : checkButton) {
                button.setChecked(button.getId() == buttonClicked.getId());
            }

            // Extract only the answer letter without the option text
            String buttonText = buttonClicked.getText().toString();
            String givenAnswer = buttonText.substring(0, 1); // Extract the first character after the period

            // Update the checked value and given answer for the current question
            questions.get(currentIndex).setCheckedValue(buttonClicked.getId());
            questions.get(currentIndex).setGivenAnswer(givenAnswer);

            // Log the updated checked value and given answer for the current question
            Log.d(TAG, "CheckedValue: " + questions.get(currentIndex).getCheckedValue());
            Log.d(TAG, "GivenAnswer: " + questions.get(currentIndex).getGivenAnswer());
        }




        @SuppressLint("SetTextI18n")
        private void setQuestionToView(Question question, int index) {
            binding.quizQuestion.setText((currentIndex + 1) + "." + question.getQuestion());
            binding.option1.setText("A. "+question.getOption1());
            binding.option2.setText("B. "+question.getOption2());
            binding.option3.setText("C. "+question.getOption3());
            binding.option4.setText("D. "+question.getOption4());

            for (RadioButton button : checkButton) {
                button.setChecked(questions.get(currentIndex).getCheckedValue() == button.getId());
            }
        }
    }
