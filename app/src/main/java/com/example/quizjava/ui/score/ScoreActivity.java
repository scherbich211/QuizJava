package com.example.quizjava.ui.score;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.MainActivity;
import com.example.quizjava.R;
import com.example.quizjava.ui.answers.AnswersActivity;
import com.example.quizjava.ui.leaderboard.LeaderboardFragment;
import com.example.quizjava.ui.questions.QuestionsActivity;
import com.example.quizjava.ui.startTest.StartTestActivity;
import com.example.quizjava.ui.test.TestActivity;

import java.util.concurrent.TimeUnit;


public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, timeTV, totalQuesTV, correctQuesTV, wrongQuesTV, unattemptedTV;
    private Button goHomeBtn, reAttemptBtn, viewAnsBtn;
    private long timeTaken;
    private Dialog progress_dialog;
    private TextView dialogText;
    private int finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress_dialog = new Dialog(ScoreActivity.this);
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        progress_dialog.show();

        init();

        loadData();

        viewAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, AnswersActivity.class);
                startActivity(intent);
            }
        });

        reAttemptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reAttempt();
            }
        });

        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        saveResult();

    }

    private void init(){
        scoreTV = findViewById(R.id.score);
        timeTV = findViewById(R.id.time);
        totalQuesTV = findViewById(R.id.total_ques);
        correctQuesTV = findViewById(R.id.correct_ques);
        wrongQuesTV = findViewById(R.id.wrong_ques);
        unattemptedTV = findViewById(R.id.un_attempted_ques);
        goHomeBtn = findViewById(R.id.home_btn);
        reAttemptBtn = findViewById(R.id.reAttempt_btn);
        viewAnsBtn = findViewById(R.id.view_answer_btn);
    }

    private void loadData(){
        int correctQ=0, wrongQ=0, unattemptedQ=0;
        for (int i = 0; i < DBQuery.g_questList.size(); i++){
            if(DBQuery.g_questList.get(i).getSelectedAns() == -1){
                unattemptedQ++;
            }else{
                if(DBQuery.g_questList.get(i).getSelectedAns() == DBQuery.g_questList.get(i).getCorrectAnswer()){
                    correctQ++;
                }else{
                    wrongQ++;
                }
            }
        }
        correctQuesTV.setText(String.valueOf(correctQ));
        wrongQuesTV.setText(String.valueOf(wrongQ));
        unattemptedTV.setText(String.valueOf(unattemptedQ));
        totalQuesTV.setText(String.valueOf(DBQuery.g_questList.size()));

        finalScore = (correctQ*100)/DBQuery.g_questList.size();
        scoreTV.setText(String.valueOf(finalScore));
        timeTaken = getIntent().getLongExtra("TIME_TAKEN",0);

        String time = String.format("%02d:%02d min",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        );
        timeTV.setText(time);
    }

    private void reAttempt(){
        for (int i = 0; i < DBQuery.g_questList.size(); i++){
            DBQuery.g_questList.get(i).setSelectedAns(-1);
            DBQuery.g_questList.get(i).setStatus(DBQuery.NOT_VISITED);
        }
        Intent intent = new Intent(ScoreActivity.this, StartTestActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveResult(){
        DBQuery.saveResult(finalScore, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                progress_dialog.dismiss();
            }
            @Override
            public void onFailure() {
                Toast.makeText(ScoreActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                progress_dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            ScoreActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}