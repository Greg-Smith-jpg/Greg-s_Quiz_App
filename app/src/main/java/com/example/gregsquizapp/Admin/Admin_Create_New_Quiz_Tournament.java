package com.example.gregsquizapp.Admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.TimeZone;

import com.example.gregsquizapp.OpenTDB.OpenTDBClient;
import com.example.gregsquizapp.OpenTDB.OpenTDBQuestion;
import com.example.gregsquizapp.OpenTDB.OpenTDBResponse;
import com.example.gregsquizapp.Questions.QuestionClass;
import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;
import com.example.gregsquizapp.Tournament.Tournaments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_Create_New_Quiz_Tournament extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerDifficulty;
    private EditText txtStartDate, txtEndDate, txtTournamentName;
    private Button btnCreateTournament;

    private String[] categories = {
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
            "Easy",
            "Medium",
            "Hard"
    };
    private int[] categoryIDs={
            9,10,11,12,17,21,22,23,27
    };
    private static final TimeZone NZ_TIME_ZONE =
            TimeZone.getTimeZone("Pacific/Auckland");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_new_quiz_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        txtTournamentName = findViewById(R.id.txtTournamentName);
        btnCreateTournament = findViewById(R.id.btnCreateTournament);

        setupSpinners();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        dateFormat.setTimeZone(NZ_TIME_ZONE);
        txtStartDate.setText(dateFormat.format(Calendar.getInstance(NZ_TIME_ZONE).getTime()));

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(txtEndDate);
            }
        });

        btnCreateTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tournamentName = txtTournamentName.getText().toString().trim();
                String startDateText = txtStartDate.getText().toString().trim();
                String endDateText = txtEndDate.getText().toString().trim();
                if(tournamentName.isEmpty()||startDateText.isEmpty()||endDateText.isEmpty()){
                    Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Please enter all fields correctly", Toast.LENGTH_SHORT).show();
                    return;
                }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
                    dateFormat.setLenient(false);
                    dateFormat.setTimeZone(NZ_TIME_ZONE);

                    try {
                        Date startDate = dateFormat.parse(startDateText);
                        Date endDate = dateFormat.parse(endDateText);

                        if(startDate == null || endDate == null || endDate.before(startDate)){
                            Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Invalid dates", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e){
                        Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Invalid dates", Toast.LENGTH_SHORT).show();
                        return;
                    }
                OpenTDBClient.getService().getQuestions(
                        10,
                        getSelectedCategoryID(),
                        getSelectedDifficulty()).enqueue(new Callback<OpenTDBResponse>() {
                    @Override
                    public void onResponse(Call<OpenTDBResponse> call, Response<OpenTDBResponse> response) {
                        if (!response.isSuccessful() || response.body() == null){
                            Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Something went wrong with OpenTDB", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        OpenTDBResponse apiResponse = response.body();
                        if ( apiResponse.getResponseCode() !=0){
                            Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "There aren;t enough of these type of questions for a quiz", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<OpenTDBQuestion> apiQuestions = apiResponse.getResults();
                        if (apiQuestions == null || apiQuestions.size() !=10){
                            Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "There aren;t enough of these type of questions for a quiz", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<QuestionClass> questionsForQuiz = new ArrayList<>();
                        for (int i = 0; i < apiQuestions.size(); i++) {
                            OpenTDBQuestion apiQuestion = apiQuestions.get(i);


                            QuestionClass question = new QuestionClass();

                            String questionText = Html.fromHtml(apiQuestion.getQuestion(), Html.FROM_HTML_MODE_LEGACY).toString();
                            String correctAnswer = Html.fromHtml(apiQuestion.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString();

                            question.setQuestion(questionText);
                            question.setCorrectAnswer(correctAnswer);
                            question.setType(apiQuestion.getType());
                            question.setOrder(i + 1);

                            List<String> answers = new ArrayList<>();
                            List<String> incorrectAnswers = apiQuestion.getIncorrectAnswers();

                            for ( int answerIndex = 0; answerIndex < incorrectAnswers.size(); answerIndex++){
                                String answer = Html.fromHtml(incorrectAnswers.get(answerIndex),Html.FROM_HTML_MODE_LEGACY).toString();

                                answers.add(answer);
                            }
                            answers.add(correctAnswer);
                            Collections.shuffle(answers);

                            question.setAnswers(answers);
                            questionsForQuiz.add(question);
                        }
                        saveTournament(tournamentName, questionsForQuiz);

                    }

                    @Override
                    public void onFailure(Call<OpenTDBResponse> call, Throwable t) {
                        Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Failed to get response from OpenTDB", Toast.LENGTH_SHORT).show();
                    }
                });
                }
        });


    }
    private void saveTournament(
            String tournamentName, List<QuestionClass> questions){
        TournamentClass tournament = new TournamentClass();
        tournament.setName(tournamentName);
        tournament.setCategoryID(getSelectedCategoryID());
        tournament.setCategoryName(spinnerCategory.getSelectedItem().toString());
        tournament.setDifficulty(getSelectedDifficulty());
        tournament.setStartDate(txtStartDate.getText().toString());
        tournament.setEndDate(txtEndDate.getText().toString());
        tournament.setQuestions(questions);

        Tournaments tournamentDatabase = new Tournaments();
        tournamentDatabase.saveTournament(tournament, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Tournament Created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Admin_Create_New_Quiz_Tournament.this, "Error saving tournament, please try again", Toast.LENGTH_SHORT).show();
                    }
                }

        );
    }

    private void setupSpinners(){
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,difficulties);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
    }
    private void showDatePicker(EditText dateField) {
        Calendar today = Calendar.getInstance(NZ_TIME_ZONE);

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            DatePicker view,
                            int year,
                            int month,
                            int dayOfMonth
                    ) {
                        Calendar selectedDate = Calendar.getInstance(NZ_TIME_ZONE);
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
                        dateFormat.setTimeZone(NZ_TIME_ZONE);

                        dateField.setText(dateFormat.format(selectedDate.getTime()));
                    }
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }
    private int getSelectedCategoryID(){
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        return categoryIDs[selectedPosition];
    }
    private String getSelectedDifficulty() {
        String difficulty = spinnerDifficulty.getSelectedItem().toString();
        return difficulty.toLowerCase(Locale.ROOT);
    }
}