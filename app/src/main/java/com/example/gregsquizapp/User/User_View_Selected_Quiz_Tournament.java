package com.example.gregsquizapp.User;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.Participation.ParticipationClass;
import com.example.gregsquizapp.Questions.QuestionClass;
import com.example.gregsquizapp.R;
import com.example.gregsquizapp.Tournament.TournamentClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class User_View_Selected_Quiz_Tournament extends AppCompatActivity {

    private TextView txtQuizTitle, txtQuestionProgress, txtQuestionText, txtFinalScore;
    private RadioGroup radioGroupAnswers;
    private RadioButton answer1, answer2, answer3, answer4;
    private Button btnSubmitAnswer, btnSubmitRating;
    private RatingBar ratingTournament;
    private View quizQuestionScreen, quizCompleteScreen;

    private FirebaseFirestore db;
    private String tournamentID, currentUserID;
    private ArrayList<QuestionClass> questions;
    private int currentQuestionIndex, score;
    private long completionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_selected_quiz_tournament);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtQuizTitle = findViewById(R.id.txtQuizTitle);
        txtQuestionProgress = findViewById(R.id.txtQuestionProgress);
        txtQuestionText = findViewById(R.id.txtQuestionText);
        txtFinalScore = findViewById(R.id.txtFinalScore);
        radioGroupAnswers = findViewById(R.id.radioGroupAnswers);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        ratingTournament = findViewById(R.id.ratingTournament);
        quizQuestionScreen = findViewById(R.id.quizQuestionScreen);
        quizCompleteScreen = findViewById(R.id.quizCompleteScreen);

        db = FirebaseFirestore.getInstance();
        tournamentID = getIntent().getStringExtra("tournamentID");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        questions = new ArrayList<>();

        btnSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer();
            }
        });

        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveParticipationTicket();
            }
        });

        loadTournament();
    }

    private void loadTournament() {
        db.collection("tournaments").document(tournamentID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        TournamentClass tournament = document.toObject(TournamentClass.class);
                        txtQuizTitle.setText(tournament.getName());

                        List<QuestionClass> savedQuestions = tournament.getQuestions();
                        for (int i = 0; i < savedQuestions.size(); i++) {
                            questions.add(savedQuestions.get(i));
                        }

                        showQuestion();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(User_View_Selected_Quiz_Tournament.this, "Could not load quiz", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void showQuestion() {
        QuestionClass question = questions.get(currentQuestionIndex);
        List<String> answers = question.getAnswers();

        txtQuestionProgress.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        txtQuestionText.setText(question.getQuestion());
        radioGroupAnswers.clearCheck();

        answer1.setVisibility(View.VISIBLE);
        answer2.setVisibility(View.VISIBLE);
        answer1.setText(answers.get(0));
        answer2.setText(answers.get(1));
        answer1.setTag("Incorrect answer");
        answer2.setTag("Incorrect answer");

        if (answers.get(0).equals(question.getCorrectAnswer())) {
            answer1.setTag("Correct answer");
        }
        if (answers.get(1).equals(question.getCorrectAnswer())) {
            answer2.setTag("Correct answer");
        }

        if (answers.size() == 2) {
            answer3.setVisibility(View.GONE);
            answer4.setVisibility(View.GONE);
        } else {
            answer3.setVisibility(View.VISIBLE);
            answer4.setVisibility(View.VISIBLE);
            answer3.setText(answers.get(2));
            answer4.setText(answers.get(3));
            answer3.setTag("Incorrect answer");
            answer4.setTag("Incorrect answer");

            if (answers.get(2).equals(question.getCorrectAnswer())) {
                answer3.setTag("Correct answer");
            }
            if (answers.get(3).equals(question.getCorrectAnswer())) {
                answer4.setTag("Correct answer");
            }
        }
    }

    private void submitAnswer() {
        if (!answer1.isChecked() && !answer2.isChecked() && !answer3.isChecked() && !answer4.isChecked()) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answer1.isChecked()) {
            checkAnswer(answer1);
        } else if (answer2.isChecked()) {
            checkAnswer(answer2);
        } else if (answer3.isChecked()) {
            checkAnswer(answer3);
        } else if (answer4.isChecked()) {
            checkAnswer(answer4);
        }

        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            showQuestion();
        } else {
            showQuizComplete();
        }
    }

    private void checkAnswer(RadioButton selectedAnswer) {
        if (selectedAnswer.getTag().equals("Correct answer")) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            QuestionClass question = questions.get(currentQuestionIndex);
            Toast.makeText(this, "Incorrect. The correct answer is: " + question.getCorrectAnswer(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuizComplete() {
        quizQuestionScreen.setVisibility(View.GONE);
        quizCompleteScreen.setVisibility(View.VISIBLE);
        txtFinalScore.setText("Your score: " + score + "/10");
        completionTime = System.currentTimeMillis();
    }

    private void saveParticipationTicket() {
        int rating = (int) ratingTournament.getRating();

        String participationID = currentUserID + "_" + tournamentID;
        ParticipationClass participationTicket = new ParticipationClass();
        participationTicket.setUserID(currentUserID);
        participationTicket.setTournamentID(tournamentID);
        participationTicket.setScore(score);
        participationTicket.setRating(rating);

        db.collection("tournaments").document(tournamentID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot tournamentDocument) {
                        TournamentClass tournament = tournamentDocument.toObject(TournamentClass.class);
                        participationTicket.setTournamentName(tournament.getName());
                        participationTicket.setCategoryName(tournament.getCategoryName());
                        participationTicket.setDifficulty(tournament.getDifficulty());
                        participationTicket.setCompletionTime(completionTime);

                        int numberOfRatings = tournament.getNumberOfRatings();
                        double averageRating = rating;

                        if (numberOfRatings > 0) {
                            averageRating = ((Double.parseDouble(tournament.getRating()) * numberOfRatings) + rating) / (numberOfRatings + 1);
                        }

                        DocumentReference participationReference = db.collection("participation").document(participationID);
                        DocumentReference userReference = db.collection("users").document(currentUserID);
                        DocumentReference tournamentReference = db.collection("tournaments").document(tournamentID);

                        participationReference.set(participationTicket);
                        userReference.update("participationTickets", FieldValue.arrayUnion(participationID));
                        tournamentReference.update("rating", String.valueOf(averageRating), "numberOfRatings", numberOfRatings + 1);

                        Toast.makeText(User_View_Selected_Quiz_Tournament.this, "Quiz completed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}