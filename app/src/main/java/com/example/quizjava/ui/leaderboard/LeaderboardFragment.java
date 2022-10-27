package com.example.quizjava.ui.leaderboard;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.DB.MyCompleteListener;
import com.example.quizjava.MainActivity;
import com.example.quizjava.R;
import com.example.quizjava.databinding.ActivityMainBinding;
import com.example.quizjava.ui.test.TestActivity;

public class LeaderboardFragment extends Fragment {

     private TextView totalUsersTV, myImgTextTV, myScoreTV, myRankTV;
     private RecyclerView usersView;
     private ActivityMainBinding binding;
     private RankAdapter adapter;
     private Dialog progress_dialog;
     private TextView dialogText;


    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Leaderboard");

        initViews(view);


        progress_dialog = new Dialog(getContext());
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        progress_dialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        usersView.setLayoutManager(layoutManager);

        adapter = new RankAdapter(DBQuery.g_usersList);
        usersView.setAdapter(adapter);

        DBQuery.getTopUsers(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                adapter.notifyDataSetChanged();
                if(DBQuery.myPerformance.getScore() != 0){
                    if(!DBQuery.isMeOnTopList){
                        calculateRank();
                    }
                    myScoreTV.setText("Score: " + DBQuery.myPerformance.getScore());
                    myRankTV.setText("Rank " + DBQuery.myPerformance.getRank());
                }
                progress_dialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "SMTH went wrong on test activity", Toast.LENGTH_SHORT).show();
                progress_dialog.dismiss();
            }
        });

        totalUsersTV.setText("Total users:" + DBQuery.g_usersCount);
        myImgTextTV.setText(DBQuery.myPerformance.getName().toUpperCase().substring(0,1));

        return view;
    }

    private void initViews(View view){
        totalUsersTV = view.findViewById(R.id.total_users);
        myImgTextTV = view.findViewById(R.id.image_text);
        myScoreTV = view.findViewById(R.id.total_score);
        myRankTV = view.findViewById(R.id.rank);
        usersView = view.findViewById(R.id.users_view);

    }

    private void calculateRank(){
        int lowTopScore = DBQuery.g_usersList.get(DBQuery.g_usersList.size() - 1).getScore();
        int remainingSlots = DBQuery.g_usersCount - 20;
        int mySlot = DBQuery.myPerformance.getScore() * remainingSlots / lowTopScore;
        int rank;
        if(lowTopScore != DBQuery.myPerformance.getScore()){
            rank = DBQuery.g_usersCount - mySlot;
        }else{
            rank = 21;
        }
        DBQuery.myPerformance.setRank(rank);
    }
}