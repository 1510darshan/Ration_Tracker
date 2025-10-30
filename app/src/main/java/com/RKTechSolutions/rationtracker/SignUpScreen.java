package com.RKTechSolutions.rationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.RKTechSolutions.rationtracker.Database.DBHelper;
import com.RKTechSolutions.rationtracker.navigation.NavigationActivity;

public class SignUpScreen extends AppCompatActivity {

    private EditText etOwnerName, etOwnerPhone, etStoreNumber;
    private LinearLayout btnRegister;
    private TextView tvLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DB
        dbHelper = DBHelper.getInstance(this);

        // Check if owner already registered
        if (dbHelper.getOwner() != null) {
            startActivity(new Intent(SignUpScreen.this, NavigationActivity.class));
            finish();
            return;
        }

        // Bind Views
        etOwnerName = findViewById(R.id.getName);
        etOwnerPhone = findViewById(R.id.getNumberSignUp);
        etStoreNumber = findViewById(R.id.getStoreNumber);
        btnRegister = findViewById(R.id.registerButton);
        tvLogin = findViewById(R.id.login);

        // Login Text Click
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpScreen.this, LogInScreen.class));
        });

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerOwner());
    }

    private void registerOwner() {
        String ownerName = etOwnerName.getText().toString().trim();
        String ownerPhone = etOwnerPhone.getText().toString().trim();
        String storeNumber = etStoreNumber.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(ownerName)) {
            etOwnerName.setError("Enter owner name");
            etOwnerName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ownerPhone) || ownerPhone.length() < 10) {
            etOwnerPhone.setError("Enter valid 10-digit phone");
            etOwnerPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(storeNumber)) {
            etStoreNumber.setError("Enter store number");
            etStoreNumber.requestFocus();
            return;
        }

        // Save to DB
        boolean success = dbHelper.registerOwner(ownerName, ownerPhone, storeNumber);

        if (success) {
            Toast.makeText(this, "Owner registered successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignUpScreen.this, NavigationActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}