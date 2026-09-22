package com.example.android.logindemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    private EditText passwordEmail;
    private EditText newPassword;
    private Button resetPassword, on;
    private SecureStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordEmail = findViewById(R.id.etPasswordEmail);
        newPassword = findViewById(R.id.etNewPasswordReset);
        resetPassword = findViewById(R.id.btnPasswordReset);
        on = findViewById(R.id.on);
        store = SecureStore.get(this);

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordActivity.this, MainActivity.class));
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = passwordEmail.getText().toString().trim();
                String pwd = newPassword.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(PasswordActivity.this, "Please enter your registered email ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length() < 8) {
                    Toast.makeText(PasswordActivity.this, "New password must be at least 8 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                // Local-only reset: only an existing account on THIS device can be reset.
                if (!store.userExists(email)) {
                    Toast.makeText(PasswordActivity.this, "No account found for that email on this device", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (store.updatePassword(email, pwd)) {
                    Toast.makeText(PasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(PasswordActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
