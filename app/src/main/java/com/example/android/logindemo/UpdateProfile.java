package com.example.android.logindemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Patterns;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {

    private EditText newUserName, newUserEmail, newUserAge;
    private Button save;
    private ImageView updateProfilePic;
    private static final int PICK_IMAGE = 123;
    private Uri imagePath;
    private SecureStore store;
    private String currentEmail;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        newUserName = findViewById(R.id.etNameUpdate);
        newUserEmail = findViewById(R.id.etEmailUpdate);
        newUserAge = findViewById(R.id.etAgeUpdate);
        save = findViewById(R.id.btnSave);
        updateProfilePic = findViewById(R.id.ivProfileUpdate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        store = SecureStore.get(this);
        currentEmail = store.getCurrentUser();

        if (currentEmail != null) {
            UserProfile p = store.getProfile(currentEmail);
            newUserName.setText(p.getUserName());
            newUserAge.setText(p.getUserAge());
            newUserEmail.setText(p.getUserEmail());
            String imgPath = store.getImagePath(currentEmail);
            if (imgPath != null && new File(imgPath).exists()) {
                updateProfilePic.setImageURI(Uri.fromFile(new File(imgPath)));
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = newUserName.getText().toString().trim();
                String age = newUserAge.getText().toString().trim();
                String email = newUserEmail.getText().toString().trim();

                if (name.isEmpty() || age.isEmpty() || email.isEmpty()) {
                    Toast.makeText(UpdateProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(UpdateProfile.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentEmail == null) {
                    Toast.makeText(UpdateProfile.this, "No active session", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Prevent colliding with a different existing account
                if (!email.equalsIgnoreCase(currentEmail) && store.userExists(email)) {
                    Toast.makeText(UpdateProfile.this, "That email is already in use", Toast.LENGTH_SHORT).show();
                    return;
                }
                store.updateProfile(currentEmail, name, age, email, imagePath);
                Toast.makeText(UpdateProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
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
