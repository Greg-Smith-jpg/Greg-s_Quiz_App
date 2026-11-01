package com.example.gregsquizapp.User;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gregsquizapp.Participation.ParticipationClass;
import com.example.gregsquizapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class User_View_Completed_Quizzes extends AppCompatActivity {

    private RecyclerView recyclerCompletedQuizzes;
    private TextView txtCompletedQuizzesMessage;
    private User_Completed_TournamentAdapter tournamentAdapter;
    private FirebaseFirestore db;
    private String currentUserID;
    private ArrayList<ParticipationClass> participationTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_completed_quizzes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerCompletedQuizzes = findViewById(R.id.recyclerCompletedQuizzes);
        txtCompletedQuizzesMessage = findViewById(R.id.txtCompletedQuizzesMessage);
        recyclerCompletedQuizzes.setLayoutManager(new LinearLayoutManager(this));

        participationTickets = new ArrayList<>();
        tournamentAdapter = new User_Completed_TournamentAdapter(participationTickets);
        recyclerCompletedQuizzes.setAdapter(tournamentAdapter);

        db = FirebaseFirestore.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCompletedQuizzes();
    }

    // gets the current users completed quiz tickets from firebase
    private void loadCompletedQuizzes() {
        txtCompletedQuizzesMessage.setVisibility(View.VISIBLE);
        txtCompletedQuizzesMessage.setText("Loading completed quizzes...");

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

                        tournamentAdapter.updateParticipationTickets(participationTickets);

                        if (participationTickets.isEmpty()) {
                            txtCompletedQuizzesMessage.setText("You have not completed any quizzes yet");
                        } else {
                            txtCompletedQuizzesMessage.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        txtCompletedQuizzesMessage.setText("Could not load completed quizzes");
                        Toast.makeText(User_View_Completed_Quizzes.this, "Could not load completed quizzes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}