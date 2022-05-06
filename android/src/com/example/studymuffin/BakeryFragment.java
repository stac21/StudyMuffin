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

public class BakeryFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_bakery, container, false);

        Context context = this.view.getContext();

        final TextView studyPointsEarnedTv = this.view.findViewById(R.id.study_points_earned_tv);
        final Button shopButton = this.view.findViewById(R.id.bakery_shop_button);

        studyPointsEarnedTv.append(" " + MainActivity.profile.getNumPoints());

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AndroidLauncher.class);

                startActivity(i);
            }
        });

        return this.view;
    }
}
