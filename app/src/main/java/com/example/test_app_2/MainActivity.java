package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    String sheetID = "1bKL7pCOHBFQTwfaL2Lr9mBMPCNagV16F7VjihIp-tYg";
    String apiKEY = BuildConfig.API_KEY;
    Button main_btn;
    int dictSize = 0;
    String strFWord;
    String strNWord;
    Random rand = new Random();
    JSONArray jsonArray;
    ListView listView;
//    CustomAdapter customAdapter;
    ArrayList<String> listForeignWords = new ArrayList<>();
    ArrayList<String> listNativeWords = new ArrayList<>();
    private static TextView twForeignWord;
    private static TextView twNativeWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_btn = findViewById(R.id.main_btn);
        twForeignWord = findViewById(R.id.foreignWord);
        twNativeWord = findViewById(R.id.nativeWord);
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWord(dictSize);
            }
        });
        String urls ="https://sheets.googleapis.com/v4/spreadsheets/" +sheetID+"/values/Sheet1?key="+apiKEY;
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


//                        customAdapter = new CustomAdapter(getApplicationContext(), listForeignWords, listNativeWords);
//                        listView.setAdapter(customAdapter);
                    } catch (Exception e) {

                    }
                });
                dictSize = listForeignWords.size();
                int val = getInt(dictSize);
                twForeignWord.setText(listForeignWords.get(val));
                twNativeWord.setText(listNativeWords.get(val));
                Log.d("My tag", String.valueOf(dictSize));

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }
    public int getInt(int size){
        return rand.nextInt(size);
    }
    public void nextWord(int dictSize){
        int val = getInt(dictSize);
        twForeignWord.setText(listForeignWords.get(val));
        twNativeWord.setText(listNativeWords.get(val));
    }
}