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
            TournamentClass tournament, OnSuccessListener<Void> successListener, OnFailureListener failureListener
    ){
        DocumentReference document = db.collection("tournaments").document();
        tournament.setTournamentID(document.getId());
        document.set(tournament).addOnSuccessListener(successListener).addOnFailureListener(failureListener);
    }
    public void loadTournaments(
        OnSuccessListener<List<TournamentClass>> successListener, OnFailureListener failureListener
    ){
        db.collection("tournaments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<TournamentClass> tournaments = new ArrayList<>();
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            for (int i = 0; i<documents.size(); i++){
                DocumentSnapshot document = documents.get(i);
                TournamentClass tournament = document.toObject(TournamentClass.class);
                if(tournament != null){
                    tournament.setTournamentID(document.getId());
                    tournaments.add(tournament);
                }
            }
            successListener.onSuccess(tournaments);
        }).addOnFailureListener(failureListener);
    }
}
