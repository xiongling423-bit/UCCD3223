package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etUserLogin, etPassword;
    Button btnLogin, btnGoToRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);
        etUserLogin = findViewById(R.id.etUserLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        btnLogin.setOnClickListener(v -> {
            String input = etUserLogin.getText().toString().trim();
            String pin = etPassword.getText().toString().trim();

            if (input.isEmpty() || pin.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.checkLogin(input);

            if (cursor != null && cursor.moveToFirst()) {
                // Get data by Column Name to avoid index errors
                int gmailIndex = cursor.getColumnIndex("gmail");
                int pinIndex = cursor.getColumnIndex("pin");

                String savedGmail = cursor.getString(gmailIndex);
                String savedPin = cursor.getString(pinIndex);

                if (pin.equals(savedPin)) {
                    // START SESSION
                    SharedPreferences prefs = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
                    prefs.edit().putString("current_session_user", savedGmail)
                            .putString("master_pin", savedPin).apply();

                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}