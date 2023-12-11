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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    public String sheetID = BuildConfig.Sheet_ID;
    String apiKEY = BuildConfig.API_KEY;
    Button  main_btn2;
    public int foreignDict = 0;
    public int nativeDict = (foreignDict - 1)*(-1);
    ImageButton btnNewWords,switchLang;
    int dictSize = 0, val,wordLength = 0,countAppearance,countRightAnswers, col, row, value,value_sum;
    public String strFWord, strNWord, wordHint, urls,sheetID_1,shtName, langNameFor, langNameNat;
    Random rand = new Random();
    JSONArray jsonArray;
    List<List<String>> listOfListsOfLists =new ArrayList<List<String>>();
    ArrayList<Integer> listMintedProb = new ArrayList<Integer>();
    ArrayList<String> listLangNames = new ArrayList<>();
    private static TextView twForeignWord,twNativeWord,twTextForeign,twTextNative;
    private static EditText editText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_btn2 = findViewById(R.id.btn_answer2);
        switchLang = findViewById(R.id.switchLang);
        twForeignWord = findViewById(R.id.foreignWord);
        twNativeWord = findViewById(R.id.nativeWord);
        twTextForeign = findViewById(R.id.textForeign);
        twTextNative = findViewById(R.id.textNative);
        editText = findViewById(R.id.answer);
        btnNewWords = findViewById(R.id.newWordsBtn);
        sheetID_1 = loadData(this,KEY);
//        sheetID_1 = sheetID;
        shtName = loadData(this,KEY_1);
        //Add first run SheetID and SheetName
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
        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLan();
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
            boolean checkAnswer = listOfListsOfLists.get(val).get(nativeDict).toUpperCase().trim().equals(editText.getText().toString().toUpperCase().trim());
            if (checkAnswer & wordLength<=1) {
                //Right answer without hints, add 1 to array of right answers
                Toast.makeText(MainActivity.this, "Wright answer ", Toast.LENGTH_SHORT).show();
                value = Integer.parseInt(listOfListsOfLists.get(val).get(3))+1;
                value_sum = Integer.parseInt(listOfListsOfLists.get(val).get(4))+1;
                listOfListsOfLists.get(val).set(3, String.valueOf(value));
                listOfListsOfLists.get(val).set(4, String.valueOf(value_sum));
                sendData(val,4,value);
                nextWord(dictSize);
            } else if (checkAnswer & wordLength>1) {
                //Wright answer, but with hint
                Toast.makeText(MainActivity.this, "Wright answer ", Toast.LENGTH_SHORT).show();
                nextWord(dictSize);
            } else {
                //Wrong answer
                Toast.makeText(MainActivity.this, "Wrong answer ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void doHintAction() {
        if (wordLength < listOfListsOfLists.get(val).get(nativeDict).length()){
            wordHint = listOfListsOfLists.get(val).get(nativeDict).substring(0, wordLength+1);
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
    public int getColToSend(){
        return this.col;
    }
    public int getRowToSend(){
        return this.row;
    }
    public int getValToSend(){
        return this.value;
    }
    public int getInt(int size){
        return rand.nextInt(size);
    }
    public int calc(int size){
        int num = 0;
        if (listMintedProb.isEmpty()) {
            int min = Integer.parseInt(listOfListsOfLists.get(0).get(4));
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(listOfListsOfLists.get(i).get(4)) < min) {
                    min = Integer.parseInt(listOfListsOfLists.get(i).get(4));
                }
            }
            for (int i =0; i<size;i++){
                if (Integer.parseInt(listOfListsOfLists.get(i).get(4)) == min) {
                    listMintedProb.add(i);
                    num = i;
                }
            }
            listMintedProb.remove(listMintedProb.size()-1);
        } else {
            int numOfElem = rand.nextInt(listMintedProb.size());
            num = listMintedProb.get(numOfElem);
            listMintedProb.remove(numOfElem);
        }
        return num;
    }
    public void nextWord(int dictSize){
//        val = getInt(dictSize);
        val = calc(dictSize);
        twNativeWord.setText("");
        editText.getText().clear();
        wordHint = String.valueOf(0);
        wordLength = 0;
        twForeignWord.setText(listOfListsOfLists.get(val).get(foreignDict));
        value = Integer.parseInt(listOfListsOfLists.get(val).get(2))+1;
        value_sum = Integer.parseInt(listOfListsOfLists.get(val).get(4))+1;
        sendData(val,3,value);
        listOfListsOfLists.get(val).set(2, String.valueOf(value));
        listOfListsOfLists.get(val).set(4, String.valueOf(value_sum));
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
                    for (int i = 0;i < len; i++){
                        List<String> listOfStrings = new ArrayList<String>();
                        try {
                            JSONArray json = jsonArray.optJSONArray(i);
                            strFWord = json.getString(0);
                            strNWord = json.getString(1);
                            if (json.length()==2){
                                json.put("0");
                                json.put("0");
                            } else if (json.length()==3){
                                json.put("0");
                            }

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
                            int value = countAppearance + countRightAnswers;
                            listOfStrings.add(strFWord);
                            listOfStrings.add(strNWord);
                            listOfStrings.add(String.valueOf(countAppearance));
                            listOfStrings.add(String.valueOf(countRightAnswers));
                            listOfStrings.add(String.valueOf(value));

                            listOfListsOfLists.add(listOfStrings);


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    listLangNames.add(listOfListsOfLists.get(0).get(0));
                    listLangNames.add(listOfListsOfLists.get(0).get(1));
                    listOfListsOfLists.remove(0);
                    dictSize = listOfListsOfLists.size();
                    twTextForeign.setText(listLangNames.get(foreignDict));
                    twTextNative.setText(listLangNames.get(nativeDict));
                    nextWord(dictSize);

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
    private void sendData(int row, int col, int value_new){
        this.row = row+2;
        this.col = col;
        this.value = value_new;
//        new SendData().execute();
        new SendMyData().execute();
    }
    public class SendMyData extends AsyncTask<String, Void, String> {
        int col = MainActivity.this.col;
        int row = MainActivity.this.row;
        int value = MainActivity.this.value;
        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL("https://script.google.com/macros/s/AKfycbyn2hquKrCHYtTv8d-CTimqbnZcr1ZoWzppd9QnORbiHd4zJkoINjmHRSksbg1YgJDNBg/exec");
//                        "https://script.google.com/macros/s/AKfycbwFAIvRwhMXr3VkLtsWgnJpODv7oQD5kruE1RSABnNrpi1H1qm6QZ-5qj6SE1F5ozzj7w/exec");
                //
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("values",value);
                postDataParams.put("shtID",sheetID_1);
                postDataParams.put("shtName",shtName);
                postDataParams.put("rowNum",row);
                postDataParams.put("colNum",col);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }
        protected void onPostExecute(String result) {
//        Toast.makeText(getApplicationContext(), "Word added",
//                Toast.LENGTH_SHORT).show();
        }
        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();
            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }

            return result.toString();

        }
    }
    public void switchLan(){
        foreignDict = (foreignDict -1)*(-1);
        nativeDict = (foreignDict - 1)*(-1);
        twTextForeign.setText(listLangNames.get(foreignDict));
        twTextNative.setText(listLangNames.get(nativeDict));
        nextWord(dictSize);
    }
}