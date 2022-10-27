package com.example.quizjava.ui.category;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.quizjava.DB.DBQuery;
import com.example.quizjava.MainActivity;
import com.example.quizjava.R;
import com.example.quizjava.databinding.ActivityMainBinding;

public class CategoryFragment extends Fragment {


    public CategoryFragment() {
    }

    private GridView categoryView;
    private ActivityMainBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        categoryView = view.findViewById(R.id.cat_Grid);
        CategoryAdapter adapter = new CategoryAdapter(DBQuery.g_catList);
        categoryView.setAdapter(adapter);

        return view;
    }


}