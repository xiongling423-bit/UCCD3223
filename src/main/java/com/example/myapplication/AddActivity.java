package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {
    EditText etSite, etUsername, etPassword, etQuestion, etAnswer;
    Button btnSave;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etSite = findViewById(R.id.etSite);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DBHelper(this);

        btnSave.setOnClickListener(v -> {
            String s = etSite.getText().toString().trim();
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();
            String q = etQuestion.getText().toString().trim();
            String a = etAnswer.getText().toString().trim();

            if (!s.isEmpty() && !u.isEmpty() && !p.isEmpty()) {
                // SESSION FIX: Get the currently logged-in user
                SharedPreferences prefs = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
                String currentUser = prefs.getString("current_session_user", "");

                // Save data linked to this user
                if (dbHelper.insertData(s, u, p, q, a, currentUser)) {
                    Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in Site, Username, and Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}