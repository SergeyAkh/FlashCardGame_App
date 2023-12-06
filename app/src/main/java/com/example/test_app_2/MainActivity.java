package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;
import java.util.ArrayList;
import java.util.stream.IntStream;


public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    String sheetID = BuildConfig.Sheet_ID;
    String apiKEY = BuildConfig.API_KEY;
    String sheetName = "Sheet1";
    Button  main_btn2;
    ImageButton btnNewWords;
    int dictSize = 0, val,wordLength = 0,countAppearance,countRightAnswers;
    String strFWord, strNWord, wordHint, urls,sheetID_1;
    Random rand = new Random();
    JSONArray jsonArray;
    ArrayList<Float> listWightedProb = new ArrayList<Float>();
    ArrayList<Integer> listCountAppearance = new ArrayList<Integer>();
    ArrayList<Integer> listCountRightAnswers = new ArrayList<Integer>();
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
        btnNewWords = findViewById(R.id.newWordsBtn);
        sheetID_1 = loadData(this,KEY);
        String shtName = loadData(this,KEY_1);
        if (sheetID_1.isEmpty()){
            Intent intent;
            intent = new Intent(MainActivity.this, AddSheetID.class);
            startActivity(intent);
        }

        urls ="https://sheets.googleapis.com/v4/spreadsheets/" + sheetID_1 +"/values/"+ shtName +"?key="+apiKEY;

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
        btnNewWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MainActivity.this, AddWords.class);
                startActivity(intent);
            }
        });
        new GetData().execute();
    }
    private void doAnswerAction() {
        if(editText.getText().toString().trim().equals("")){
            Toast.makeText(MainActivity.this, "Type your answer", Toast.LENGTH_LONG).show();}
        else {
            boolean checkAnswer = listNativeWords.get(val).toString().toUpperCase().equals(editText.getText().toString().toUpperCase().trim());
            if (checkAnswer & wordLength==0) {
                //Right answer without hints, add 1 to array of right answers
                Toast.makeText(MainActivity.this, "Wright answer ", Toast.LENGTH_SHORT).show();
                nextWord(dictSize);
                twNativeWord.setText("");
                editText.getText().clear();
                listCountRightAnswers.set(val,listCountRightAnswers.get(val)+1);
            } else if (checkAnswer & wordLength>0) {
                //Wright answer, but with hint
                Toast.makeText(MainActivity.this, "Wright answer ", Toast.LENGTH_SHORT).show();
                nextWord(dictSize);
                wordLength = 0;
                wordHint = String.valueOf(0);
                twNativeWord.setText("");
                editText.getText().clear();
            } else {
                //Wrong answer
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
    public static String loadData(Context context,String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(key, "");
        return text;
    }

    public int getInt(int size){
        return rand.nextInt(size);
    }
    public float calc(int size){
        for (int i=0;i<size;i++){
            double y = listCountAppearance.get(i) + listCountRightAnswers.get(i) + 1;
            listWightedProb.set(i, (float) Math.pow(y,-1));
        }
        Float max = listWightedProb.get(0);
        int num = 0;
        for (int i = 1; i < size; i++)
            if (listWightedProb.get(i) > max)
                num = i;
        return num;
    }

    public void nextWord(int dictSize){
        val = getInt(dictSize);
        twForeignWord.setText(listForeignWords.get(val));
        listCountAppearance.set(val,listCountAppearance.get(val)+1);
//        long intSum = listCountRightAnswers.stream()
//                .mapToLong(Integer::longValue)
//                .sum();
//        Log.d("my tag", String.valueOf(intSum));
    }


    public class GetData extends AsyncTask<String, Void, String >{

        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urls, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonArray = response.optJSONArray("values");
                    int len = jsonArray.length();

                    for (int i = 1;i < len; i++){
                        try {
                            JSONArray json = jsonArray.optJSONArray(i);
                            strFWord = json.getString(0);
                            strNWord = json.getString(1);
                            try {
                                countAppearance = json.getInt(2);
                            } catch (Exception e){
                                countAppearance = 0;
                            }
                            try {
                                countRightAnswers = json.getInt(3);
                            }catch (Exception e){
                                countRightAnswers = 0;
                            }
                            listCountAppearance.add(countAppearance);
                            listCountRightAnswers.add(countRightAnswers);
                            listForeignWords.add(strFWord);
                            listNativeWords.add(strNWord);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    dictSize = listForeignWords.size();
                    val = getInt(dictSize);
                    twForeignWord.setText(listForeignWords.get(val));
                    listCountAppearance.set(val,listCountAppearance.get(val)+1);
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

}