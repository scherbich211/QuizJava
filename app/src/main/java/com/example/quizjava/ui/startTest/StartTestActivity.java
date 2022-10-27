package com.example.quizjava.ui.startTest;

import static com.example.quizjava.DB.DBQuery.g_catList;
import static com.example.quizjava.DB.DBQuery.loadQuestions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.R;
import com.example.quizjava.ui.questions.QuestionsActivity;
import com.example.quizjava.ui.test.TestActivity;

public class StartTestActivity extends AppCompatActivity {

    private TextView catName, testNo, totalQuestion, bestScore, time;
    private Button startTestBtn;
    private ImageView backBtn;

    private Dialog progress_dialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        init();

        progress_dialog = new Dialog(StartTestActivity.this);
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        progress_dialog.show();

        loadQuestions(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                setData();
                progress_dialog.dismiss();
            }

            @Override
            public void onFailure() {
                progress_dialog.dismiss();
                Toast.makeText(StartTestActivity.this, "SMTH went wrong on start test",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        catName = findViewById(R.id.st_cat_name);
        testNo = findViewById(R.id.st_test_NO);
        totalQuestion = findViewById(R.id.st_total_questions);
        bestScore = findViewById(R.id.st_best_score);
        time = findViewById(R.id.st_time);
        startTestBtn = findViewById(R.id.st_btn);
        backBtn = findViewById(R.id.st_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTestActivity.this.finish();
            }
        });
        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartTestActivity.this, QuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setData(){
        catName.setText(g_catList.get(DBQuery.g_selected_cat_index).getName());
        testNo.setText("Test No. " + String.valueOf(DBQuery.g_selected_test_index + 1));
        totalQuestion.setText(String.valueOf(DBQuery.g_questList.size()));
        bestScore.setText(String.valueOf(DBQuery.g_testList.get(DBQuery.g_selected_test_index).getTopScore()));
        time.setText(String.valueOf(DBQuery.g_testList.get(DBQuery.g_selected_test_index).getTime()));
    }
}