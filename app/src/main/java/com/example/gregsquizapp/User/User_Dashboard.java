package com.example.gregsquizapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;
import com.example.gregsquizapp.Tournament.Tournaments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class User_Dashboard extends AppCompatActivity {

    private static final ZoneId NZ_TIME_ZONE = ZoneId.of("Pacific/Auckland");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT);

    private RecyclerView recyclerUserTournaments;
    private TextView txtTournamentListMessage;
    private Spinner spinnerTournamentType;
    private Spinner spinnerTournamentCategory;
    private Spinner spinnerTournamentDifficulty;
    private Button btnFilterTournaments, btnViewMyStats;

    private String[] tournamentTypes = {
            "All statuses",
            "Ongoing",
            "Upcoming",
            "Past"
    };

    private String[] categories = {
            "All categories",
            "General Knowledge",
            "Books",
            "Film",
            "Music",
            "Science & Nature",
            "Sports",
            "Geography",
            "History",
            "Animals"
    };

    private String[] difficulties = {
            "All difficulties",
            "Easy",
            "Medium",
            "Hard"
    };

    private ArrayList<TournamentClass> allTournaments;
    private User_TournamentAdapter tournamentAdapter;
    private Tournaments tournamentDatabase;
    private FirebaseFirestore db;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerUserTournaments = findViewById(R.id.recyclerUserTournaments);
        txtTournamentListMessage = findViewById(R.id.txtTournamentListMessage);
        spinnerTournamentType = findViewById(R.id.spinnerTournamentType);
        spinnerTournamentCategory = findViewById(R.id.spinnerTournamentCategory);
        spinnerTournamentDifficulty = findViewById(R.id.spinnerTournamentDifficulty);
        btnFilterTournaments = findViewById(R.id.btnFilterTournaments);
        btnViewMyStats = findViewById(R.id.btnViewMyStats);

        recyclerUserTournaments.setLayoutManager(new LinearLayoutManager(this));

        allTournaments = new ArrayList<>();
        ArrayList<TournamentClass> displayedTournaments = new ArrayList<>();
        tournamentAdapter = new User_TournamentAdapter(displayedTournaments);
        recyclerUserTournaments.setAdapter(tournamentAdapter);
        tournamentAdapter.setOnTournamentClickListener(new User_TournamentAdapter.OnTournamentClickListener() {
            @Override
            public void onTournamentClick(TournamentClass tournament) {
                openTournament(tournament);
            }
        });

        tournamentDatabase = new Tournaments();
        db = FirebaseFirestore.getInstance();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupSpinners();

        btnFilterTournaments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectedTournaments();
            }
        });

        btnViewMyStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Dashboard.this, User_View_My_Stats.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTournaments();
    }

    private void setupSpinners() {
        ArrayAdapter<String> tournamentTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tournamentTypes);
        tournamentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournamentType.setAdapter(tournamentTypeAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournamentCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTournamentDifficulty.setAdapter(difficultyAdapter);

    }

    private void openTournament(TournamentClass tournament) {
        LocalDate today = getTodayDate();
        LocalDate startDate = getDate(tournament.getStartDate());
        LocalDate endDate = getDate(tournament.getEndDate());

        if (today.isBefore(startDate) || today.isAfter(endDate)) {
            Toast.makeText(this, "You can only participate in an ongoing tournament", Toast.LENGTH_SHORT).show();
            return;
        }

        String participationID = currentUserID + "_" + tournament.getTournamentID();

        db.collection("users").document(currentUserID).get()
                .addOnSuccessListener(userDocument -> {
                    List<String> participationTickets = (List<String>) userDocument.get("participationTickets");

                    if (participationTickets != null && participationTickets.contains(participationID)) {
                        Toast.makeText(User_Dashboard.this, "You have already participated in this tournament", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(User_Dashboard.this, User_View_Selected_Quiz_Tournament.class);
                        intent.putExtra("tournamentID", tournament.getTournamentID());
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(User_Dashboard.this, "Could not check participation", Toast.LENGTH_SHORT).show());
    }

    private void loadTournaments() {
        txtTournamentListMessage.setVisibility(View.VISIBLE);
        txtTournamentListMessage.setText("Loading tournaments...");

        tournamentDatabase.loadTournaments(new OnSuccessListener<List<TournamentClass>>() {
                    @Override
                    public void onSuccess(List<TournamentClass> loadedTournaments) {
                        allTournaments.clear();

                        for (int i = 0; i < loadedTournaments.size(); i++) {
                            allTournaments.add(loadedTournaments.get(i));
                        }

                        showAllTournaments();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtTournamentListMessage.setVisibility(View.VISIBLE);
                        txtTournamentListMessage.setText("Could not load tournaments");
                        Toast.makeText(User_Dashboard.this, "Error loading tournaments", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showAllTournaments() {
        ArrayList<TournamentClass> displayedTournaments = new ArrayList<>();

        for (int i = 0; i < allTournaments.size(); i++) {
            displayedTournaments.add(allTournaments.get(i));
        }

        tournamentAdapter.updateTournaments(displayedTournaments);

        if (displayedTournaments.isEmpty()) {
            txtTournamentListMessage.setVisibility(View.VISIBLE);
            txtTournamentListMessage.setText("No tournaments available");
        } else {
            txtTournamentListMessage.setVisibility(View.GONE);
        }
    }

    private void showSelectedTournaments() {
        String selectedType = spinnerTournamentType.getSelectedItem().toString();
        String selectedCategory = spinnerTournamentCategory.getSelectedItem().toString();
        String selectedDifficulty = spinnerTournamentDifficulty.getSelectedItem().toString();

        ArrayList<TournamentClass> selectedTournaments = new ArrayList<>();
        LocalDate today = getTodayDate();

        for (int i = 0; i < allTournaments.size(); i++) {
            TournamentClass tournament = allTournaments.get(i);
            boolean matchesType = selectedType.equals("All statuses");

            if (!matchesType) {
                LocalDate startDate = getDate(tournament.getStartDate());
                LocalDate endDate = getDate(tournament.getEndDate());

                if (selectedType.equals("Upcoming") && today.isBefore(startDate)) {
                    matchesType = true;
                } else if (selectedType.equals("Ongoing") && !today.isBefore(startDate) && !today.isAfter(endDate)) {
                    matchesType = true;
                } else if (selectedType.equals("Past") && today.isAfter(endDate)) {
                    matchesType = true;
                }
            }

            boolean matchesCategory = selectedCategory.equals("All categories") || selectedCategory.equals(tournament.getCategoryName());

            String tournamentDifficulty = tournament.getDifficulty();

            boolean matchesDifficulty = selectedDifficulty.equals("All difficulties") || (selectedDifficulty.equalsIgnoreCase(tournamentDifficulty));

            if (matchesType && matchesCategory && matchesDifficulty) {
                selectedTournaments.add(tournament);
            }
        }

        tournamentAdapter.updateTournaments(selectedTournaments);

        if (selectedTournaments.isEmpty()) {
            txtTournamentListMessage.setVisibility(View.VISIBLE);
            txtTournamentListMessage.setText("No tournaments match these filters");
        } else {
            txtTournamentListMessage.setVisibility(View.GONE);
        }
    }

    private LocalDate getTodayDate() {
        return LocalDate.now(NZ_TIME_ZONE);
    }

    private LocalDate getDate(String dateText) {
        return LocalDate.parse(dateText, DATE_FORMAT);
    }
}
