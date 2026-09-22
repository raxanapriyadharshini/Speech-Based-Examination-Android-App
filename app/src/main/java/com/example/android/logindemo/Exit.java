package com.example.android.logindemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class Exit extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button bttnClr;
    private TextView resultText,textView ,text ;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_main);


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    double  result;
                    String reg;
                    result = Marks.marks;
                    reg = Marks.regno;
                    resultText.setText(String.valueOf(result));
                    text.setText(String.valueOf(reg));
                    {
                        if(result>2)
                        {
                            textView.setText("pass");
                        }
                        else {
                            textView.setText("fail");
                        }
                    }

            }
        },5000);
        firebaseAuth = FirebaseAuth.getInstance();
        bttnClr = findViewById(R.id.buttonClear);
        resultText = findViewById(R.id.textViewResult);
        textView = findViewById(R.id.textView2);
        text = findViewById(R.id.textView4);
        bttnClr.setOnClickListener(new ClickButton());
    }

    private class ClickButton implements Button.OnClickListener{

        public void onClick(View v){
            if (v.getId() == R.id.buttonClear){
                resultText.setText("0");
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Exit.this, MainActivity.class));
            }
        }

    }
}