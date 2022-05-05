package com.example.studymuffin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        final TextView desc = this.findViewById(R.id.noteDescriptionTV);

        String description = "Binary search is a search algorithm that runs in O(log(n)) on average." +
                "Best case the algorithm has a time complexity of O(1), if the element being searched" +
                "for is the first element that the algorithm comes across. It works by splitting up " +
                "the problem in half continuously until the element being searched for is found. " +
                "An example of the algorithm is below.";

        desc.setText(description);
    }
}
