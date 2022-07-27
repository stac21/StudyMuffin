package com.example.studymuffin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mygdx.studymuffin.BakeryDemo;

public class BakeryFragment extends Fragment {
    private View view;
    public static String MONEY_KEY = "com.example.studymuffin.bakery_money";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_bakery, container, false);

        final Button shopButton = this.view.findViewById(R.id.bakery_shop_button);

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BakeryDemo.studyPoints = MainActivity.profile.getNumPoints();
                Intent i = new Intent(v.getContext(), AndroidLauncher.class);

                startActivity(i);
            }
        });

        return this.view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView studyPointsEarnedTv = this.view.findViewById(R.id.study_points_earned_tv);
        studyPointsEarnedTv.setText(R.string.study_points_earned);
        studyPointsEarnedTv.append(" " + MainActivity.profile.getNumPoints());
    }
}
