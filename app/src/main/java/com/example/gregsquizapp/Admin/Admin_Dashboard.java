package com.example.gregsquizapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.Main.Login;
import com.example.gregsquizapp.Main.Register;
import com.example.gregsquizapp.R;

public class Admin_Dashboard extends AppCompatActivity {
    TextView txtCreateNewQuizTournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCreateNewQuizTournament = findViewById(R.id.txtCreateNewQuizTournament);

        txtCreateNewQuizTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_Create_New_Quiz_Tournament.class);
                startActivity(intent);
            }
        });
    }


}