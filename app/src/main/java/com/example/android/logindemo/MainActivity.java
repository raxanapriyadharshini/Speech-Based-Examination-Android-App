package com.example.android.logindemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private TextView forgotPassword;
    private SecureStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Info = findViewById(R.id.tvInfo);
        Login = findViewById(R.id.btnLogin);
        userRegistration = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.tvForgotPassword);

        Info.setText("No of attempts remaining: 5");

        store = SecureStore.get(this);

        // If a session already exists, skip the login screen.
        if (store.isLoggedIn()) {
            finish();
            startActivity(new Intent(MainActivity.this, Tans.class));
            return;
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString().trim(), Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });
    }

    private void validate(String userEmail, String userPassword) {
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (store.authenticate(userEmail, userPassword)) {
            store.setCurrentUser(userEmail);
            finish();
            startActivity(new Intent(MainActivity.this, Tans.class));
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            counter--;
            Info.setText("No of attempts remaining: " + counter);
            if (counter <= 0) {
                Login.setEnabled(false);
            }
        }
    }
}
