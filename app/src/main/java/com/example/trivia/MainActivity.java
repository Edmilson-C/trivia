package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SCORE_ID = "score";
    private TextView tvCounter, tvQuestion, tvHighScore, tvCurrentScore;
    private Button butTrue, butFalse, butShare;
    private ImageButton butPrev, butNext;
    private List<Question> questionList;
    private int currentQuestionIndex, currentScore;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCounter = findViewById(R.id.tvCounter);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvCurrentScore = findViewById(R.id.tvCurrentScore);
        butTrue = findViewById(R.id.butTrue);
        butFalse = findViewById(R.id.butFalse);
        butPrev = findViewById(R.id.butPrev);
        butNext = findViewById(R.id.butNext);
        butShare = findViewById(R.id.butShare);
        prefs = new Prefs(MainActivity.this);
        currentQuestionIndex = prefs.getQuestionIndex();
        currentScore = 0;

        butTrue.setOnClickListener(this);
        butFalse.setOnClickListener(this);
        butPrev.setOnClickListener(this);
        butNext.setOnClickListener(this);
        butShare.setOnClickListener(this);
        tvCurrentScore.setText("Current Score: " + currentScore);
        tvHighScore.setText("High Score: " + prefs.getHighScore());

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse(){
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                tvQuestion.setText(questionArrayList.get(currentQuestionIndex).getQuestion());
                tvCounter.setText((currentQuestionIndex+1) + "/" + questionArrayList.size());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.saveData(currentScore, currentQuestionIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butFalse :
                checkAnswer(false);
                break;
            case R.id.butTrue :
                checkAnswer(true);
                break;
            case R.id.butPrev :
                if(currentQuestionIndex == 0)
                    currentQuestionIndex = questionList.size();
                updateQuestion(currentQuestionIndex-1);
                break;
            case R.id.butNext :
                updateQuestion(currentQuestionIndex+1);
                break;
            case R.id.butShare :
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Yoo! I'm playing Trivia");
                intent.putExtra(Intent.EXTRA_TEXT, "My Current Score is: " + currentScore + " and my High Score is: " + prefs.getHighScore());
                startActivity(intent);
                break;
        }
    }

    public void updateQuestion(int index) {
        currentQuestionIndex = index % questionList.size();
        tvQuestion.setText(questionList.get(currentQuestionIndex).getQuestion());
        tvCounter.setText((currentQuestionIndex+1) + "/" + questionList.size());
    }

    public void checkAnswer(boolean choice) {
        if(choice == questionList.get(currentQuestionIndex).getAnswer()) {
            fadeView();
            currentScore += 10;
            tvCurrentScore.setText("Current Score: " + currentScore);
        } else {
            shakeAnimation();
            if(currentScore > 0)
                currentScore -= 10;
            tvCurrentScore.setText("Current Score: " + currentScore);
        }
    }

    private void fadeView() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        final CardView cardView = findViewById(R.id.cardView);
        alphaAnimation.setDuration(400);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                updateQuestion(currentQuestionIndex+1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                updateQuestion(currentQuestionIndex+1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
