package com.example.android.logindemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePassword extends AppCompatActivity {

    private Button update;
    private EditText newPassword;
    private SecureStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        update = findViewById(R.id.btnUpdatePassword);
        newPassword = findViewById(R.id.etNewPassword);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        store = SecureStore.get(this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPasswordNew = newPassword.getText().toString();
                String currentEmail = store.getCurrentUser();

                if (currentEmail == null) {
                    Toast.makeText(UpdatePassword.this, "No active session", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userPasswordNew.length() < 8) {
                    Toast.makeText(UpdatePassword.this, "Password must be at least 8 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                if (store.updatePassword(currentEmail, userPasswordNew)) {
                    Toast.makeText(UpdatePassword.this, "Password Changed", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdatePassword.this, "Password Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
