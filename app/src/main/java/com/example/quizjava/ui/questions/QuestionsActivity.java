package com.example.quizjava.ui.questions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.R;
import com.example.quizjava.ui.score.ScoreActivity;

import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView questionsView;
    private TextView tvQuestID, timerTV, catNameTV;
    private Button submitBtn, markBtn, clearBtn;
    private ImageButton prevQuesBtn, nextQuesBtn;
    private ImageView quesListBtn;
    private int quesID;
    private DrawerLayout drawer;
    private ImageButton drawerCloseBtn;
    private GridView quesListGV;
    QuestionsAdapter questionsAdapter;
    private ImageView markImage;
    private QuestionGridAdapter gridAdapter;
    private CountDownTimer timer;
    private long timeLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_list_layout);

        init();

        questionsAdapter = new QuestionsAdapter(DBQuery.g_questList);
        questionsView.setAdapter(questionsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionsView.setLayoutManager(layoutManager);

        gridAdapter = new QuestionGridAdapter(this, DBQuery.g_questList.size());
        quesListGV.setAdapter(gridAdapter);

        setSnapHelper();

        setClickListener();

        startTimer();

    }
    private void init(){
        questionsView = findViewById(R.id.questions_view);
        tvQuestID = findViewById(R.id.tv_questID);
        timerTV = findViewById(R.id.tv_timer);
        catNameTV = findViewById(R.id.qa_catName);
        submitBtn = findViewById(R.id.submit);
        markBtn = findViewById(R.id.mark_btn);
        clearBtn = findViewById(R.id.clear_sel_btn);
        prevQuesBtn = findViewById(R.id.prev_ques_btn);
        nextQuesBtn = findViewById(R.id.next_ques_btn);
        quesListBtn = findViewById(R.id.ques_list_grid_btn);

        drawer = findViewById(R.id.drawer_layout);
        drawerCloseBtn = findViewById(R.id.drawer_close_btn);

        markImage = findViewById(R.id.mark_image);
        quesListGV = findViewById(R.id.ques_list_gv);

        quesID = 0;
        tvQuestID.setText("1/" + String.valueOf(DBQuery.g_questList.size()));
        catNameTV.setText(DBQuery.g_catList.get(DBQuery.g_selected_cat_index).getName());

        DBQuery.g_questList.get(0).setStatus(DBQuery.UNANSWERED);
    }


    private void setSnapHelper(){
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionsView);

        questionsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                quesID = recyclerView.getLayoutManager().getPosition(view);

                if(DBQuery.g_questList.get(quesID).getStatus() == DBQuery.NOT_VISITED){
                    DBQuery.g_questList.get(quesID).setStatus(DBQuery.UNANSWERED);
                }

                if(DBQuery.g_questList.get(quesID).getStatus() == DBQuery.REVIEW){
                    markImage.setVisibility(View.VISIBLE);
                }else{
                    markImage.setVisibility(View.GONE);
                }

                tvQuestID.setText(String.valueOf(quesID + 1) + "/" + String.valueOf(DBQuery.g_questList.size()));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void setClickListener(){
        prevQuesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quesID > 0){
                    questionsView.smoothScrollToPosition(quesID - 1);
                }
            }
        });

        nextQuesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quesID < DBQuery.g_questList.size() - 1) {
                    questionsView.smoothScrollToPosition(quesID + 1);
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBQuery.g_questList.get(quesID).setSelectedAns(-1);
                DBQuery.g_questList.get(quesID).setStatus(DBQuery.UNANSWERED);
                markImage.setVisibility(View.GONE);
                questionsAdapter.notifyDataSetChanged();
            }
        });

        quesListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawer.isDrawerOpen(GravityCompat.END)){
                    gridAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        drawerCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });

        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markImage.getVisibility() != View.VISIBLE){
                    markImage.setVisibility(View.VISIBLE);
                    DBQuery.g_questList.get(quesID).setStatus(DBQuery.REVIEW);
                }else{
                    markImage.setVisibility(View.GONE);
                    if(DBQuery.g_questList.get(quesID).getSelectedAns() != -1){
                        DBQuery.g_questList.get(quesID).setStatus(DBQuery.ANSWERED);
                    }else{
                        DBQuery.g_questList.get(quesID).setStatus(DBQuery.UNANSWERED);
                    }
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTest();
            }
        });
    }

    private void submitTest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layout,null);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        Button confirmBtn = view.findViewById(R.id.confirm_btn);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                alertDialog.dismiss();

                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long totalTime = DBQuery.g_testList.get(DBQuery.g_selected_test_index).getTime() * 60 * 1000;
                intent.putExtra("TIME_TAKEN",totalTime-timeLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        });
        alertDialog.show();
    }

    public void goToQuestion(int position){
        questionsView.smoothScrollToPosition(position);
        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }
    }

    private void startTimer(){
        long totalTime = DBQuery.g_testList.get(DBQuery.g_selected_test_index).getTime() * 60 * 1000;
        timer = new CountDownTimer(totalTime + 1000, 1000) {
            @Override
            public void onTick(long remainingTime) {
                timeLeft = remainingTime;
                String time = String.format("%02d:%02d min",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                );
                timerTV.setText(time);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long totalTime = DBQuery.g_testList.get(DBQuery.g_selected_test_index).getTime() * 60 * 1000;
                intent.putExtra("TIME_TAKEN",totalTime-timeLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        };
        timer.start();
    }
}