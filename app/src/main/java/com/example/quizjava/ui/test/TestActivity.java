package com.example.quizjava.ui.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.R;


public class TestActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Toolbar toolbar;
    private TestAdapter adapter;
    private Dialog progress_dialog;
    private TextView dialogText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testView = findViewById(R.id.test_recycler_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(DBQuery.g_catList.get(DBQuery.g_selected_cat_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress_dialog = new Dialog(TestActivity.this);
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        progress_dialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testView.setLayoutManager(layoutManager);

        DBQuery.loadTestData(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                DBQuery.loadMyScores(new MyCompleteListener() {
                    @Override
                    public void onSuccess() {
                        adapter = new TestAdapter(DBQuery.g_testList);
                        testView.setAdapter(adapter);
                        progress_dialog.dismiss();
                    }
                    @Override
                    public void onFailure() {
                        progress_dialog.dismiss();
                        Toast.makeText(TestActivity.this, "SMTH went wrong on test activity",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure() {
                progress_dialog.dismiss();
                Toast.makeText(TestActivity.this, "SMTH went wrong on test activity",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            TestActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}