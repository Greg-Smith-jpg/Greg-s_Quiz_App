package com.example.gregsquizapp.Admin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;

import java.util.ArrayList;

public class Admin_TournamentAdapter extends RecyclerView.Adapter<Admin_Tournament_ViewHolder> {

    private ArrayList<TournamentClass> tournaments;

    public Admin_TournamentAdapter(ArrayList<TournamentClass> tournaments) {
        this.tournaments = tournaments;
    }

    @NonNull
    @Override
    public Admin_Tournament_ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_admin_tournament_card,
                parent,
                false
        );

        return new Admin_Tournament_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull Admin_Tournament_ViewHolder holder,
            int position
    ) {
        TournamentClass tournament = tournaments.get(position);

        holder.txtTournamentName.setText(tournament.getName());
        holder.txtTournamentCategory.setText(tournament.getCategoryName());
        holder.txtTournamentDifficulty.setText(tournament.getDifficulty());
        holder.txtTournamentStartDate.setText(tournament.getStartDate());
        holder.txtTournamentEndDate.setText(tournament.getEndDate());
        holder.txtTournamentRating.setText("Not yet rated");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),Admin_View_Selected_Quiz_Tournament.class);
                intent.putExtra("tournamentID", tournament.getTournamentID());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    public void updateTournaments(ArrayList<TournamentClass> newTournaments) {
        tournaments.clear();

        for (int i = 0; i < newTournaments.size(); i++) {
            tournaments.add(newTournaments.get(i));
        }

        notifyDataSetChanged();
    }
}