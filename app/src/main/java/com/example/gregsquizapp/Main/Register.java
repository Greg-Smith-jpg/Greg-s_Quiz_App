package com.example.gregsquizapp.Main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gregsquizapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    EditText txtUsername, txtEmail, txtPassword, txtConfirmPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button btnCreateNewAccount, btnCancelNewAccount;
    String user_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtUsername = findViewById(R.id.txtCreateNewUsername);
        txtPassword = findViewById(R.id.txtCreateNewPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmNewPassword);
        txtEmail = findViewById(R.id.txtCreateNewEmail);
        btnCreateNewAccount = findViewById(R.id.btnCreateNewAccount);
        btnCancelNewAccount = findViewById(R.id.btnCancelNewAccount);
    }
}