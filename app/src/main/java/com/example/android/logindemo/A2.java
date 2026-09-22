package com.example.android.logindemo;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class A2 extends AppCompatActivity {

    public TextView txv;
    String[] t1 = {"blue whale"};
    public static int marks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a1);
        txv = (TextView) findViewById(R.id.txv);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txv.setText(result.get(0));
                }
                break;
        }
        for (int i = 0; i < t1.length; i++) { //May also use buttonArray.length
            String currentString = t1[i];
            if (t1[i].equals(txv.getText().toString())) {
                final Intent next;
                next = new Intent(A2.this, Q3.class);
                Marks.marks = Marks.marks  + 1;
                Toast toast = Toast.makeText(getApplicationContext(), "marks is "+Marks.marks, Toast.LENGTH_LONG);
                toast.setGravity(50, 350, 250);
                toast.show();
                startActivity(next);
            }
            if (!t1[i].equals(txv.getText().toString())) {
                final Intent next;
                next = new Intent(A2.this, Q3.class);
                Toast.makeText(this, "wornge ans", Toast.LENGTH_LONG).show();
                startActivity(next);
            }
        }

    }
}
