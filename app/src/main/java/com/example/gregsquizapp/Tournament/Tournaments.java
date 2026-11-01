package com.example.gregsquizapp.Tournament;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Tournaments {
    private final FirebaseFirestore db;

    public Tournaments(){
        db = FirebaseFirestore.getInstance();
    }


    public void saveTournament(
            //creates a new tournament in firebase using the questions from the api call with the selected details by the admin
            TournamentClass tournament, OnSuccessListener<Void> successListener, OnFailureListener failureListener
    ){
        DocumentReference document = db.collection("tournaments").document();
        tournament.setTournamentID(document.getId());
        document.set(tournament).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }

    public void loadTournaments(
            // gets all tournaments in firebase and converts them into a tournamnetClass object
            // uses its firebase id as its tournamentID
            // adds them to a list then passes the list when called
        OnSuccessListener<List<TournamentClass>> successListener, OnFailureListener failureListener
    ){
        db.collection("tournaments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<TournamentClass> tournaments = new ArrayList<>();
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            for (int i = 0; i<documents.size(); i++){
                DocumentSnapshot document = documents.get(i);
                TournamentClass tournament = document.toObject(TournamentClass.class);
                tournament.setTournamentID(document.getId());
                tournaments.add(tournament);
            }
            successListener.onSuccess(tournaments);
        }).addOnFailureListener(failureListener);
    }
}
