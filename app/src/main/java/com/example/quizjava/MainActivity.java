package com.example.quizjava;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.quizjava.ui.account.AccountFragment;
import com.example.quizjava.ui.leaderboard.LeaderboardFragment;
import com.example.quizjava.ui.category.CategoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizjava.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private FrameLayout main_fraim;

    private BottomNavigationView bottomNavigationView;

    private TextView drawerProfileName, drawerProfileText;

    private NavigationBarView.OnItemSelectedListener onNavigationItemSelectedListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()){
                        case R.id.navigation_home:
                            setFragment(new CategoryFragment());
                            return true;
                        case R.id.navigation_leaderboard:
                            setFragment(new LeaderboardFragment());
                            return true;
                        case R.id.navigation_account:
                            setFragment(new AccountFragment());
                            return true;

                    }

                    return false;
                }
            };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Categories");


        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        main_fraim = findViewById(R.id.main_frame);
        bottomNavigationView.setOnItemSelectedListener(onNavigationItemSelectedListener);

        setFragment(new CategoryFragment());
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main_fraim.getId(),fragment);
        transaction.commit();
    }
}