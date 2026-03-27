package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText etRegUser, etRegGmail, etRegPin;
    Button btnSaveAccount;
    DBHelper dbHelper; // Added DBHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);
        etRegUser = findViewById(R.id.etRegUser);
        etRegGmail = findViewById(R.id.etRegGmail);
        etRegPin = findViewById(R.id.etRegPin);
        btnSaveAccount = findViewById(R.id.btnSaveAccount);

        btnSaveAccount.setOnClickListener(v -> {
            String user = etRegUser.getText().toString().trim();
            String gmail = etRegGmail.getText().toString().trim();
            String pin = etRegPin.getText().toString().trim();
            String gmailPattern = "[a-zA-Z0-9._-]+@gmail\\.com";

            if (user.isEmpty() || gmail.isEmpty() || pin.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
            else if (!gmail.matches(gmailPattern)) {
                etRegGmail.setError("Must be a valid @gmail.com address");
            }
            else {
                // FIXED: Save to Database instead of SharedPreferences
                if (dbHelper.registerUser(user, gmail, pin)) {
                    Toast.makeText(this, "Account Registered Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Registration Failed or Gmail already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}