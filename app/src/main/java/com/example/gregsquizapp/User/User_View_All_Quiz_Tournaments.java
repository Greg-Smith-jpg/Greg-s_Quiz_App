package com.example.gregsquizapp.User;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.R;

public class User_View_All_Quiz_Tournaments extends AppCompatActivity {

    //TODO View all tournaments in a recycler view, ability to filter tournaments by type (ongoing, upcoming, past, participated),
    // date, category and difficulty
    // ability to search for tournaments by name
    // tap a tournament to view its details page
    // sort by latest by default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_quiz_tournaments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}