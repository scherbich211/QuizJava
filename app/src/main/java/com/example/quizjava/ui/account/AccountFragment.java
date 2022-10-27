package com.example.quizjava.ui.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.example.quizjava.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;



public class AccountFragment extends Fragment {

    private LinearLayout logoOutBtn, leaderboard;
    private TextView profile_image_text, name, score, rank;
    private BottomNavigationView bottomNavigationView;
    private ActivityMainBinding binding;
    private Dialog progress_dialog;
    private TextView dialogText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);

        progress_dialog = new Dialog(getContext());
        progress_dialog.setContentView(R.layout.dialog_layout);
        progress_dialog.setCancelable(false);
        progress_dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progress_dialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing in...");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("My Account");

        String userName = DBQuery.myProfile.getName();
        profile_image_text.setText(userName.toUpperCase().substring(0,1));
        name.setText(userName);

        score.setText(String.valueOf(DBQuery.myPerformance.getScore()));

        if(DBQuery.g_usersList.size() == 0){
            progress_dialog.show();
            DBQuery.getTopUsers(new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    if(DBQuery.myPerformance.getScore() != 0){
                        if(!DBQuery.isMeOnTopList){
                            calculateRank();
                        }
                        score.setText("Score:" + DBQuery.myPerformance.getScore());
                        rank.setText("Rank " + DBQuery.myPerformance.getRank());
                    }
                    progress_dialog.dismiss();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), "SMTH went wrong on test activity", Toast.LENGTH_SHORT).show();
                    progress_dialog.dismiss();
                }
            });
        }else{
            score.setText("Score: " + DBQuery.myPerformance.getScore());
            if(DBQuery.myPerformance.getScore() != 0) {
                rank.setText("Rank " + DBQuery.myPerformance.getRank());
            }
        }


        logoOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_leaderboard);
            }
        });

        return view;

    }

    private void initViews(View view){
        profile_image_text = view.findViewById(R.id.profile_img_text);
        name = view.findViewById(R.id.name);
        score = view.findViewById(R.id.total_score);
        rank = view.findViewById(R.id.rank);
        leaderboard = view.findViewById(R.id.leaderboard_btn);
        logoOutBtn = view.findViewById(R.id.logout_btn);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_bar);
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