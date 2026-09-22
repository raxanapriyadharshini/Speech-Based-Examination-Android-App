package com.example.android.logindemo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Q2 extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech mTextToSpeech;
    private Button mButton;
    private EditText mEditText;
    int i;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q2);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final  Intent intent ;
                intent = new Intent(Q2.this, A2.class);
                startActivity(intent);
            }
        },5000);

        mButton = findViewById(R.id.speak);
        mEditText = findViewById(R.id.textToSpeak);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final  Intent intent ;
                intent = new Intent(Q2.this, A2.class);
                startActivity(intent);
            }
        });

        mTextToSpeech = new TextToSpeech(this, this);
    }


    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
//            mTextToSpeech.setSpeechRate(0.5F);
//            mTextToSpeech.setPitch(2F);
            //setting language to Indonesia
            int result = mTextToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("SimpleTTS", "Language not supported!");
                mButton.setEnabled(false);
            } else {
                mButton.setEnabled(true);
                i=i+1;
                speakNow();
            }
        } else {
            Log.e("SimpleTTS", "Initializing failed");
        }
    }

    private void speakNow() {
        String str = mEditText.getText().toString();
        if (!str.isEmpty()) {
            mTextToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
