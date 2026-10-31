package com.example.gregsquizapp.Admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Admin_View_Selected_Quiz_Tournament extends AppCompatActivity {

    private static final TimeZone NZ_TIME_ZONE = TimeZone.getTimeZone("Pacific/Auckland");

    private EditText txtTournamentName,txtTournamentStartDate,txtTournamentEndDate;
    private TextView txtTournamentCategory,txtTournamentRating,txtTournamentDifficulty;
    private Button btnSaveTournamentChanges,btnDeleteTournament;
    private FirebaseFirestore db;

    private String tournamentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_selected_quiz_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtTournamentName = findViewById(R.id.txtSelectedTournamentName);
        txtTournamentCategory = findViewById(R.id.txtSelectedTournamentCategory);
        txtTournamentDifficulty = findViewById(R.id.txtSelectedTournamentDifficulty);
        txtTournamentRating = findViewById(R.id.txtSelectedTournamentRating);
        txtTournamentStartDate = findViewById(R.id.txtSelectedTournamentStartDate);
        txtTournamentEndDate = findViewById(R.id.txtSelectedTournamentEndDate);
        btnSaveTournamentChanges = findViewById(R.id.btnSaveTournamentChanges);
        btnDeleteTournament = findViewById(R.id.btnDeleteTournament);

        db = FirebaseFirestore.getInstance();
        tournamentID = getIntent().getStringExtra("tournamentID");
        loadTournamentDetails();

        txtTournamentStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(txtTournamentStartDate);
            }
        });
        txtTournamentEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(txtTournamentEndDate);
            }
        });
        btnSaveTournamentChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTournamentChanges();
            }
        });
        btnDeleteTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmTournamentDeletion();
            }
        });
    }

    private void loadTournamentDetails(){
        db.collection("tournaments").document(tournamentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                TournamentClass tournament = document.toObject(TournamentClass.class);
                txtTournamentName.setText(tournament.getName());
                txtTournamentCategory.setText(tournament.getCategoryName());
                txtTournamentDifficulty.setText(tournament.getDifficulty());
                txtTournamentStartDate.setText(tournament.getStartDate());
                txtTournamentEndDate.setText(tournament.getEndDate());
                if(tournament.getNumberOfRatings()==0){
                    txtTournamentRating.setText("Not yet rated");
                }else{
                    txtTournamentRating.setText(tournament.getRating());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Error loading tournament", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDatePicker(EditText dateField){
        Calendar today = Calendar.getInstance(NZ_TIME_ZONE);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance(NZ_TIME_ZONE);
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
                dateField.setText(dateFormat.format(selectedDate.getTime()));
            }
        },
        today.get(Calendar.YEAR),
        today.get(Calendar.MONTH),
        today.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void saveTournamentChanges(){
        String tournamentName = txtTournamentName.getText().toString().trim();
        String startDateText = txtTournamentStartDate.getText().toString().trim();
        String endDateText = txtTournamentEndDate.getText().toString().trim();

        if(tournamentName.isEmpty()||startDateText.isEmpty()||endDateText.isEmpty()){
            Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Please enter details correctly", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        dateFormat.setLenient(false);
        dateFormat.setTimeZone(NZ_TIME_ZONE);

        try{
            Date startDate = dateFormat.parse(startDateText);
            Date endDate = dateFormat.parse(endDateText);

            if(endDate.before(startDate)){
                Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                return;
            }
        }catch(ParseException e){
            Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Enter valid dates",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> changes = new HashMap<>();
            changes.put("name", tournamentName);
            changes.put("startDate", startDateText);
            changes.put("endDate", endDateText);

            db.collection("tournaments").document(tournamentID).update(changes).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Tournament updated",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Tournament updated",Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void confirmTournamentDeletion(){
        new AlertDialog.Builder(this).setTitle("Delete Tournament?")
                .setMessage("Are you sure you want to delete this tournament?\nThis cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTournament();
            }
        }).setNegativeButton("Cancel", null).show();
        }

        private void deleteTournament() {
            db.collection("tournaments").document(tournamentID).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Tournament deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Admin_View_Selected_Quiz_Tournament.this, "Could not delete tournament", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


}