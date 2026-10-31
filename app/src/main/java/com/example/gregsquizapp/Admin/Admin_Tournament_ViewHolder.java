package com.example.gregsquizapp.Admin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gregsquizapp.R;

public class Admin_Tournament_ViewHolder extends RecyclerView.ViewHolder {

    TextView txtTournamentName;
    TextView txtTournamentCategory;
    TextView txtTournamentDifficulty;
    TextView txtTournamentStartDate;
    TextView txtTournamentEndDate;
    TextView txtTournamentRating;

    public Admin_Tournament_ViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTournamentName = itemView.findViewById(R.id.txt_tournament_name);
        txtTournamentCategory = itemView.findViewById(R.id.txt_tournament_category);
        txtTournamentDifficulty = itemView.findViewById(R.id.txt_tournament_difficulty);
        txtTournamentStartDate = itemView.findViewById(R.id.txt_tournament_start_date);
        txtTournamentEndDate = itemView.findViewById(R.id.txt_tournament_end_date);
        txtTournamentRating = itemView.findViewById(R.id.txt_tournament_rating);
    }
}