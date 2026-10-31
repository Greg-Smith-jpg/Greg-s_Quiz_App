package com.example.gregsquizapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gregsquizapp.Main.Login;
import com.example.gregsquizapp.Main.Register;
import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;
import com.example.gregsquizapp.Tournament.Tournaments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_Dashboard extends AppCompatActivity {

    private RecyclerView recyclerAllTournaments;
    private ArrayList<TournamentClass> tournamentList;
    private Admin_TournamentAdapter tournamentAdapter;
    private Tournaments tournamentDatabase;
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

        recyclerAllTournaments = findViewById(R.id.recyclerAllTournaments);
        recyclerAllTournaments.setLayoutManager(new LinearLayoutManager(this));

        tournamentList = new ArrayList<>();
        tournamentAdapter = new Admin_TournamentAdapter(tournamentList);
        recyclerAllTournaments.setAdapter(tournamentAdapter);

        tournamentDatabase = new Tournaments();

        txtCreateNewQuizTournament = findViewById(R.id.txtCreateNewQuizTournament);

        txtCreateNewQuizTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_Dashboard.this, Admin_Create_New_Quiz_Tournament.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadTournaments();
    }

    private void loadTournaments(){
        tournamentDatabase.loadTournaments(
                new OnSuccessListener<List<TournamentClass>>() {
                    @Override
                    public void onSuccess(List<TournamentClass> loadedTournaments) {
                        ArrayList<TournamentClass> newTournamentList = new ArrayList<>();
                        for (int i = 0; i < loadedTournaments.size(); i++) {
                            newTournamentList.add(loadedTournaments.get(i));
                        }
                        tournamentAdapter.updateTournaments(newTournamentList);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Admin_Dashboard.this, "Error loading tournaments", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


}