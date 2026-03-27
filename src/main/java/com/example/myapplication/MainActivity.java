package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button btnAdd;
    DBHelper dbHelper;
    ArrayList<String> list;
    ArrayList<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        dbHelper = new DBHelper(this);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        list = new ArrayList<>();
        idList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
        // This will now always return the unique Gmail
        String currentUser = prefs.getString("current_session_user", "");

        Cursor cursor = dbHelper.getAllData(currentUser);

        while (cursor.moveToNext()) {
            idList.add(cursor.getString(0));
            String display = "Site name: " + cursor.getString(1) +
                    "\nUser name: " + cursor.getString(2) +
                    "\nPassword: ****";
            list.add(display);
        }

        CustomAdapter adapter = new CustomAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void showReAuthDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Check");
        builder.setMessage("Enter Master Password to reveal and edit info");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            SharedPreferences prefs = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
            String savedPin = prefs.getString("master_pin", "");

            if (input.getText().toString().equals(savedPin)) {
                revealData(position);
            } else {
                Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void revealData(int position) {
        SharedPreferences prefs = getSharedPreferences("AUTH_PREFS", MODE_PRIVATE);
        String currentUser = prefs.getString("current_session_user", "");

        Cursor cursor = dbHelper.getAllData(currentUser);
        cursor.moveToPosition(position);

        final String id = cursor.getString(0);
        String site = cursor.getString(1);
        String user = cursor.getString(2);
        String pass = dbHelper.decrypt(cursor.getString(3));
        String question = cursor.getString(4);
        String answer = cursor.getString(5);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        layout.addView(createLabel("Site Name:"));
        final EditText etSite = new EditText(this); etSite.setText(site); layout.addView(etSite);

        layout.addView(createLabel("User Name / Email:"));
        final EditText etUser = new EditText(this); etUser.setText(user); layout.addView(etUser);

        layout.addView(createLabel("Password:"));
        final EditText etPass = new EditText(this); etPass.setText(pass); layout.addView(etPass);

        layout.addView(createLabel("Challenge Question:"));
        final EditText etQues = new EditText(this); etQues.setText(question); layout.addView(etQues);

        layout.addView(createLabel("Answer:"));
        final EditText etAns = new EditText(this); etAns.setText(answer); layout.addView(etAns);

        new AlertDialog.Builder(this)
                .setTitle("Edit Credentials")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    boolean updated = dbHelper.updateData(id,
                            etSite.getText().toString(),
                            etUser.getText().toString(),
                            etPass.getText().toString(),
                            etQues.getText().toString(),
                            etAns.getText().toString(),
                            currentUser);

                    if (updated) {
                        Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private TextView createLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(0, 20, 0, 0);
        return tv;
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        public CustomAdapter(Context context, ArrayList<String> items) {
            super(context, R.layout.list_item, items);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView detailsText = convertView.findViewById(R.id.tvEntryDetails);
            ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteEntry);

            detailsText.setText(getItem(position));
            detailsText.setOnClickListener(v -> showReAuthDialog(position));

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to remove this entry?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            dbHelper.deleteData(idList.get(position));
                            Toast.makeText(MainActivity.this, "Entry Removed", Toast.LENGTH_SHORT).show();
                            loadData();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            return convertView;
        }
    }
}