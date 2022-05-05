package com.example.studymuffin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BakeryFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_bakery, container, false);

        final Button shopButton = this.view.findViewById(R.id.bakery_shop_button);
        final ImageView bakery = this.view.findViewById(R.id.bakery);
        final ImageView bakeryEmpty = this.view.findViewById(R.id.bakery_empty);

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bakery.setVisibility(View.VISIBLE);
                bakeryEmpty.setVisibility(View.INVISIBLE);
                shopButton.setText("Exit shop");
            }
        });

        return this.view;
    }
}
