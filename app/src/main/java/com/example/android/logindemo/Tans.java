package com.example.android.logindemo;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Objects;
import java.util.Timer;

public class Tans extends AppCompatActivity {
        EditText username,password;
        Button login;
    String[] t1 = {"4080","4084"};
    String[] t2 = {"07111998","07031999"};
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.actans);
            username=findViewById(R.id.username);
            password=findViewById(R.id.password);
            login=findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String testu =username.getText().toString();
                    String testp=password.getText().toString();
                    for (int i = 0; i < t1.length; i++) {
                        if ((t1[i].equals (testu))&&(t2[i].equals(testp)))
                        {
                            Marks.regno = t1[i];
                            Toast.makeText(Tans.this, "You have Authenticated Successfully", Toast.LENGTH_LONG).show();
                            final Intent next;
                            next = new Intent(Tans.this, SecondActivity.class);
                            startActivity(next);
                        }
                    }
                }
            });
        }
    }