package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Random;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    String sheetID = BuildConfig.Sheet_ID;
    String apiKEY = BuildConfig.API_KEY;
    Button  main_btn2;
    int dictSize = 0, val,wordLength = 0;
    String strFWord, strNWord, wordHint, urls;
    Random rand = new Random();
    JSONArray jsonArray;
    ArrayList<String> listForeignWords = new ArrayList<>();
    ArrayList<String> listNativeWords = new ArrayList<>();
    private static TextView twForeignWord,twNativeWord;
    private static EditText editText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_btn2 = findViewById(R.id.btn_answer2);
        twForeignWord = findViewById(R.id.foreignWord);
        twNativeWord = findViewById(R.id.nativeWord);
        editText = findViewById(R.id.answer);

        String urls ="https://sheets.googleapis.com/v4/spreadsheets/" +sheetID+"/values/Sheet1?key="+apiKEY;
        new loadItems().execute();

        Drawable drawable = editText.getBackground(); // get current EditText drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if (Build.VERSION.SDK_INT > 16) {
            editText.setBackground(drawable); // set the new drawable to EditText
        } else {
            editText.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
        main_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAnswerAction();
            }
        });

        twNativeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doHintAction();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urls, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonArray = response.getJSONArray("values");
                } catch (Exception e) {
                }
                IntStream.range(1, jsonArray.length()).forEach(i -> {
                    try {
                        JSONArray json = jsonArray.getJSONArray(i);
                        strFWord = json.getString(0);
                        strNWord = json.getString(1);
                        listForeignWords.add(strFWord);
                        listNativeWords.add(strNWord);
                    } catch (Exception e) {

                    }
                });
                dictSize = listForeignWords.size();
                val = getInt(dictSize);
                twForeignWord.setText(listForeignWords.get(val));
//                twNativeWord.setText(listNativeWords.get(val));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }
    private void doAnswerAction() {
        if(editText.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this, "Type your answer", Toast.LENGTH_LONG).show();}
        else {
            if (listNativeWords.get(val).toString().toUpperCase().equals(editText.getText().toString().toUpperCase().trim())) {
                Toast.makeText(MainActivity.this, "Wright answer ", Toast.LENGTH_LONG).show();
                nextWord(dictSize);
                wordLength = 0;
                wordHint = String.valueOf(0);
                twNativeWord.setText("");
                editText.getText().clear();
            } else {
                Toast.makeText(MainActivity.this, "Wrong answer ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void doHintAction() {
        if (wordLength < listNativeWords.get(val).length()){
            wordHint = listNativeWords.get(val).substring(0, wordLength+1);
            twNativeWord.setText(wordHint);
            wordLength++;
        }
        else{
            Toast.makeText(MainActivity.this, "Whole word is being shown ", Toast.LENGTH_SHORT).show();
        }
    }


    public class loadItems extends AsyncTask<String,String,String> {
        int dictSize = 0, val,wordLength = 0;
        String strFWord, strNWord, wordHint, urls;
        Random rand = new Random();
        JSONArray jsonArray;
        ArrayList<String> listForeignWords = new ArrayList<>();
        ArrayList<String> listNativeWords = new ArrayList<>();
        protected String doInBackground(String... args){
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urls, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        jsonArray = response.getJSONArray("values");
                    } catch (Exception e) {
                    }
                    IntStream.range(1, jsonArray.length()).forEach(i -> {
                        try {
                            JSONArray json = jsonArray.getJSONArray(i);
                            strFWord = json.getString(0);
                            strNWord = json.getString(1);
                            listForeignWords.add(strFWord);
                            listNativeWords.add(strNWord);
                        } catch (Exception e) {

                        }
                    });
                    dictSize = listForeignWords.size();
                    val = getInt(dictSize);
                    twForeignWord.setText(listForeignWords.get(val));
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
            return null;
        }

    }
//    public void loadCards(int dictSize){
//        modelArrayList = new ArrayList<>();
//        Log.d("My tag load",String.valueOf(listForeignWords));
//        modelArrayList.add(new MyModel("kajha","aavuv"));
//        modelArrayList.add(new MyModel("arbab","rebart"));
//    }
    public int getInt(int size){
        return rand.nextInt(size);
    }
    public void nextWord(int dictSize){
        val = getInt(dictSize);
        twForeignWord.setText(listForeignWords.get(val));
    }
}