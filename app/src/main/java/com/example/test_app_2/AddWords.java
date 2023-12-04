package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class AddWords extends AppCompatActivity {
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);
        String sheetID_1 = MainActivity.loadData(this,KEY);
        String shtName = MainActivity.loadData(this,KEY_1);
        Button submit = findViewById(R.id.submitNewWords);
        EditText foreignNewWord = findViewById(R.id.newForeignWord);
        EditText nativeNewWord = findViewById(R.id.newNativeWord);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}