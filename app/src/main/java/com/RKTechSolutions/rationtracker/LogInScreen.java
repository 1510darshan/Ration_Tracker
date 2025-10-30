package com.RKTechSolutions.rationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.RKTechSolutions.rationtracker.navigation.NavigationActivity;

public class LogInScreen extends AppCompatActivity {

    TextView RegisterText;
    LinearLayout loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RegisterText = findViewById(R.id.register);
        loginbtn = findViewById(R.id.loginButton);

        Intent SignUpActivity = new Intent( LogInScreen.this, SignUpScreen.class);
        Intent NextActivity = new Intent( LogInScreen.this, NavigationActivity.class);

        loginbtn.setOnClickListener(v -> {
            startActivity(NextActivity);
        });

        RegisterText.setOnClickListener(v -> {
            startActivity(SignUpActivity);
        });
    }
}