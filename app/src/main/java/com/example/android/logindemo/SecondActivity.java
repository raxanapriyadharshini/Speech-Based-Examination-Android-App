package com.example.android.logindemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SecondActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button logout;
    Spinner mSpinner;
    TextView mOutputSpinnerTv;
    //options to be displayed in spinner
    String[] mOptions = {"Select the Subject",  "English", "Tamil","Maths", "Science","Social"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        firebaseAuth = FirebaseAuth.getInstance();
        logout= findViewById(R.id.button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });


        mSpinner = findViewById(R.id.spinner);
        mOutputSpinnerTv = findViewById(R.id.mOutputSpinnerTv);

        //Creating the ArrayAdapter instance having the list of options
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting the ArrayAdapter data on the Spinner
        mSpinner.setAdapter(aa);

        //spinner item click handler
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final Intent intent;
                switch(position) {
                    case 1:
                        mOutputSpinnerTv.setText("English is selected...");
                        intent = new Intent(SecondActivity.this, Q1.class);
                        startActivity(intent);
                        break;
                    case 2:

                        mOutputSpinnerTv.setText("Tamil is selected...");
                        intent = new Intent(SecondActivity.this, Underdep.class);
                        startActivity(intent);
                        break;
                    case 3:
                        mOutputSpinnerTv.setText("Maths is selected...");
                        intent = new Intent(SecondActivity.this, Underdep.class);
                        startActivity(intent);
                        break;

                    case 4:
                        mOutputSpinnerTv.setText("Science is selected...");
                        intent = new Intent(SecondActivity.this, Underdep.class);
                        startActivity(intent);
                        break;
                    case 5 :
                       mOutputSpinnerTv.setText("Social is selected...");
                        intent = new Intent(SecondActivity.this, Underdep.class);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logoutMenu:{
                Logout();
                break;
            }
            case R.id.profileMenu:
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
