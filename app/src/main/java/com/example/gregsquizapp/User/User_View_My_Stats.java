package com.example.gregsquizapp.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.Participation.ParticipationClass;
import com.example.gregsquizapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class User_View_My_Stats extends AppCompatActivity {

    private TextView txtCompletedQuizCount, txtAverageScore, txtAverageRating;
    private LinearLayout layoutAverageScorePerCategory, layoutAverageScorePerDifficulty;
    private Button btnViewCompletedQuizzes;
    private FirebaseFirestore db;
    private String currentUserID;
    private ArrayList<ParticipationClass> participationTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_my_stats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCompletedQuizCount = findViewById(R.id.txtCompletedQuizCount);
        txtAverageScore = findViewById(R.id.txtAverageScore);
        txtAverageRating = findViewById(R.id.txtAverageRating);
        layoutAverageScorePerCategory = findViewById(R.id.layoutAverageScorePerCategory);
        layoutAverageScorePerDifficulty = findViewById(R.id.layoutAverageScorePerDifficulty);
        btnViewCompletedQuizzes = findViewById(R.id.btnViewCompletedQuizzes);

        btnViewCompletedQuizzes.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Intent intent = new Intent(User_View_My_Stats.this, User_View_Completed_Quizzes.class);
                startActivity(intent);
            }
        });

        participationTickets = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadParticipationTickets();
    }

    private void loadParticipationTickets() {
        db.collection("participation").whereEqualTo("userID", currentUserID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        participationTickets.clear();
                        List<DocumentSnapshot> ticketDocuments = querySnapshot.getDocuments();

                        for (int i = 0; i < ticketDocuments.size(); i++) {
                            ParticipationClass ticket = ticketDocuments.get(i).toObject(ParticipationClass.class);
                            participationTickets.add(ticket);
                        }

                        updateStats();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(User_View_My_Stats.this, "Could not load stats", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStats() {
        layoutAverageScorePerCategory.removeAllViews();
        layoutAverageScorePerDifficulty.removeAllViews();

        int totalScore = 0;
        int totalRating = 0;
        Map<String, Integer> categoryScores = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();
        Map<String, Integer> difficultyScores = new HashMap<>();
        Map<String, Integer> difficultyCounts = new HashMap<>();

        for (int i = 0; i < participationTickets.size(); i++) {
            ParticipationClass ticket = participationTickets.get(i);
            totalScore = totalScore + ticket.getScore();
            totalRating = totalRating + ticket.getRating();

            String category = ticket.getCategoryName();
            if (categoryScores.containsKey(category)) {
                categoryScores.put(category, categoryScores.get(category) + ticket.getScore());
                categoryCounts.put(category, categoryCounts.get(category) + 1);
            } else {
                categoryScores.put(category, ticket.getScore());
                categoryCounts.put(category, 1);
            }

            String difficulty = ticket.getDifficulty().toLowerCase(Locale.ROOT);
            if (difficultyScores.containsKey(difficulty)) {
                difficultyScores.put(difficulty, difficultyScores.get(difficulty) + ticket.getScore());
                difficultyCounts.put(difficulty, difficultyCounts.get(difficulty) + 1);
            } else {
                difficultyScores.put(difficulty, ticket.getScore());
                difficultyCounts.put(difficulty, 1);
            }
        }

        txtCompletedQuizCount.setText("Quizzes completed: " + participationTickets.size());

        if (participationTickets.isEmpty()) {
            txtAverageScore.setText("Average score: N/A");
            txtAverageRating.setText("Average rating: N/A");
        } else {
            txtAverageScore.setText("Average score: " + formatAverage((double) totalScore / participationTickets.size()) + "/10");
            txtAverageRating.setText("Average rating: " + formatAverage((double) totalRating / participationTickets.size()) + "/5");
        }

        String[] categories = {
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

        LinearLayout categoryRow = new LinearLayout(this);
        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];

            if (i % 2 == 0) {
                categoryRow = new LinearLayout(this);
                categoryRow.setOrientation(LinearLayout.HORIZONTAL);
                layoutAverageScorePerCategory.addView(categoryRow);
            }

            LinearLayout categoryStats = new LinearLayout(this);
            categoryStats.setOrientation(LinearLayout.VERTICAL);

            int categoryPadding = (int) (8 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams categoryLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            categoryLayoutParams.setMargins(categoryPadding, categoryPadding / 2, categoryPadding, categoryPadding / 2);
            categoryStats.setLayoutParams(categoryLayoutParams);
            categoryStats.setPadding(categoryPadding, categoryPadding, categoryPadding, categoryPadding);

            addAverageLine(categoryStats, category, true);

            int quizCount = categoryCounts.containsKey(category) ? categoryCounts.get(category) : 0;
            addAverageLine(categoryStats, "Quizzes taken: " + quizCount, false);

            if (quizCount == 0) {
                addAverageLine(categoryStats, "Average score: N/A", false);
            } else {
                double average = (double) categoryScores.get(category) / quizCount;
                addAverageLine(categoryStats, "Average score: " + formatAverage(average) + "/10", false);
            }

            categoryRow.addView(categoryStats);
        }

        String[] difficulties = {
                "Easy",
                "Medium",
                "Hard"
        };

        for (int i = 0; i < difficulties.length; i++) {
            String difficulty = difficulties[i];
            String difficultyKey = difficulty.toLowerCase(Locale.ROOT);
            int quizCount = difficultyCounts.containsKey(difficultyKey) ? difficultyCounts.get(difficultyKey) : 0;

            addAverageLine(layoutAverageScorePerDifficulty, difficulty + " quizzes taken: " + quizCount, false);

            if (quizCount == 0) {
                addAverageLine(layoutAverageScorePerDifficulty, difficulty + " quiz average score: N/A", false);
            } else {
                double average = (double) difficultyScores.get(difficultyKey) / quizCount;
                addAverageLine(layoutAverageScorePerDifficulty, difficulty + " quiz average score: " + formatAverage(average) + "/10", false);
            }
        }
    }

    private void addAverageLine(LinearLayout layout, String text, boolean bold) {
        TextView averageLine = new TextView(this);
        averageLine.setText(text);

        if (bold) {
            averageLine.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        layout.addView(averageLine);
    }

    private String formatAverage(double average) {
        return String.format(Locale.ROOT, "%.1f", average);
    }
}