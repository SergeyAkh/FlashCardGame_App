package com.example.test_app_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    private static final String KEY_lang = "foreign_lang";
    String apiKEY = BuildConfig.API_KEY;
    String FOREIGN_LANG_KEY = "LangForeign";
    String NATIVE_LANG_KEY = "LangNative";
    String CURRENT_VALUE_WORD = "CurrentPosition";
    ImageButton main_btn2;
    ImageButton switchLang, btnMenu,nxtWord,learnedButton;
    int dictSize = 0, val, wordLength = 0, countAppearance, countRightAnswers, col, row, value, value_sum, langValue,actionChose;
    public String strFWord, strNWord, wordHint, urls, sheetID_1, shtName;
    Random rand = new Random();
    JSONArray jsonArray;
    List<List<String>> listOfListsOfLists = new ArrayList<List<String>>();
    ArrayList<Integer> listMintedProb = new ArrayList<Integer>();
    ArrayList<String> listLangNames = new ArrayList<>();
    private static TextView twForeignWord, twNativeWord, twTextForeign, twTextNative;
    private static EditText editText;
    boolean nextWordPressed = false;
    int foreignDict = 0;
    int nativeDict = (foreignDict - 1) * (-1);
    String urlSendData = "https://script.google.com/macros/s/AKfycbxKm3b1SXhf9x2HOalTxwYh1w-xtNFMShsqIWQb_615Ncf1JNGV0e1J7HB9CrWVRU6-Og/exec";
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FOREIGN_LANG_KEY, twTextForeign.getText().toString());
        outState.putString(NATIVE_LANG_KEY, twTextNative.getText().toString());
        outState.putInt(CURRENT_VALUE_WORD, val);
        super.onSaveInstanceState(outState);
        // call superclass to save any view hierarchy
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        main_btn2 = findViewById(R.id.btn_answer2);
        switchLang = findViewById(R.id.switchLang);
        btnMenu = findViewById(R.id.btnMenu);
        learnedButton = findViewById(R.id.btnLearned);
        twForeignWord = findViewById(R.id.foreignWord);
        nxtWord = findViewById(R.id.btnNextWord);
        twNativeWord = findViewById(R.id.nativeWord);
        twTextForeign = findViewById(R.id.textForeign);
        twTextNative = findViewById(R.id.textNative);
        editText = findViewById(R.id.answer);
        try {
            foreignDict = loadData_lang(MainActivity.this, KEY_lang);
            nativeDict = (foreignDict - 1) * (-1);
        } catch (Exception e){

        }
        sheetID_1 = loadData(this, KEY);
        shtName = loadData(this, KEY_1);
        //Add first run SheetID and SheetName

        if (sheetID_1.isEmpty()) {
            Intent intent;
            intent = new Intent(MainActivity.this, AddSheetID.class);
            startActivity(intent);
        }
        urls = "https://sheets.googleapis.com/v4/spreadsheets/" + sheetID_1 + "/values/" + shtName + "?key=" + apiKEY;
        Drawable drawable = editText.getBackground(); // get current EditText drawable
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if (Build.VERSION.SDK_INT > 16) {
            editText.setBackground(drawable); // set the new drawable to EditText
        } else {
            editText.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
        PopupMenu popup = new PopupMenu(MainActivity.this, btnMenu);
        popup.getMenuInflater().inflate(R.menu.menu,popup.getMenu());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        learnedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!haveNetworkConnection()){
                    showToast("Check Internet Connections");
                }else {
                    builder.setTitle("Learned");
                    builder.setMessage("The word is learned and will be deleted from dictionary.\nAre you sure?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCurrentWord();
                            nextWord(dictSize);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        nxtWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!haveNetworkConnection()){
                    showToast("Check Internet Connections");
                } else {
                    nextWordPressed = true;
                    nextWord(dictSize);
                    nextWordPressed = false;
                }
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent = new Intent(MainActivity.this, EditWord.class);
                        Bundle b = new Bundle();
                        int id = menuItem.getItemId();
                        if (id == R.id.addNewWord){
                            if (!haveNetworkConnection()){
                                showToast("Check Internet Connections");
                            } else {
//                                Intent intent = new Intent(MainActivity.this, EditWord.class);
                                b.putInt("row_num", val);
                                b.putString("URL",urlSendData);
                                b.putInt("action",3);
                                intent.putExtras(b);
                                startActivity(intent);
                            }

                        } else if (id == R.id.editCurWord) {
                            if (!haveNetworkConnection()){
                                showToast("Check Internet Connections");
                            } else {
                                b.putInt("row_num", val);
                                b.putString("oldForeignWord", listOfListsOfLists.get(val).get(foreignDict));
                                b.putString("oldNativeWord", listOfListsOfLists.get(val).get(nativeDict));
                                b.putString("URL", urlSendData);
                                b.putInt("action", 0);
                                intent.putExtras(b);
                                startActivity(intent);
                            }

                        } else if (id == R.id.clearAllHistory){
                            if (!haveNetworkConnection()){
                                showToast("Check Internet Connections");
                            } else {
                                builder.setTitle("Clear statistics");
                                builder.setMessage("Clear all statistics for whole dictionary.\nAre you sure?");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        clearAllHistory();
                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });
                            }
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
//                        else if (id == R.id.changeTable){
//                            builder.setTitle("Change dictionaries");
//                            builder.setMessage("This requires you to enter URL or Sheet Name to new dictionary.\nAre you sure?");
//                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (!haveNetworkConnection()){
//                                        showToast("Check Internet Connections");
//                                    } else {
//                                        Intent intent = new Intent(MainActivity.this, AddSheetID.class);
//                                        startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//
//                                }
//                            });
//
//                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // Do nothing
//                                    dialog.dismiss();
//                                }
//                            });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//                        }
                        return true;
                    }
                });

                popup.show();

            }
        });

        main_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!haveNetworkConnection()){
                    showToast("Check Internet Connections");
                } else {
                    doAnswerAction();
                }

            }
        });
        switchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!haveNetworkConnection()) {
                    showToast("Check Internet Connections");
                } else {
                    switchLan();
                }
            }
        });
        twNativeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!haveNetworkConnection()) {
                    showToast("Check Internet Connection");
                } else {
                    doHintAction();
                }
            }
        });

        if (!haveNetworkConnection()) {
            showToast("Check Internet Connection");
        } else {
            new GetData().execute();
        }

    }
    public void clearAllHistory(){
        sendData(1,0,0,2);
        showToast("History is cleared");
    }
    public void deleteCurrentWord(){
        listOfListsOfLists.remove(val);
        sendData(val,1,1,1);
        dictSize = listOfListsOfLists.size();
    }

    public void showToast(String txt){
        View v = View.inflate(this, R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));
        TextView text = (TextView) v.findViewById(R.id.textForToast);
        text.setText(txt);
        Toast toast = new Toast(MainActivity.this);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();

//        Toast.makeText(getApplicationContext(), txt,
//                Toast.LENGTH_SHORT).show();
    }

    private void doAnswerAction() {
        if (editText.getText().toString().trim().equals("")) {
            showToast("Type your answer");
        } else {
            boolean checkAnswer = listOfListsOfLists.get(val).get(nativeDict).toUpperCase().trim().equals(editText.getText().toString().toUpperCase().trim());
            if (checkAnswer & wordLength <= 1) {
                //Right answer without hints, add 1 to array of right answers
                showToast("Wright answer");
                value = Integer.parseInt(listOfListsOfLists.get(val).get(3)) + 1;
                value_sum = Integer.parseInt(listOfListsOfLists.get(val).get(4)) + 1;
                listOfListsOfLists.get(val).set(3, String.valueOf(value));
                listOfListsOfLists.get(val).set(4, String.valueOf(value_sum));
                sendData(val, 4, value,0);
                nextWord(dictSize);
            } else if (checkAnswer & wordLength > 1) {
                //Wright answer, but with hint
                showToast("Wright answer");
                nextWord(dictSize);
            } else {
                //Wrong answer
                showToast("Wrong answer");
            }
        }
    }
    public void doHintAction() {
        if (wordLength < listOfListsOfLists.get(val).get(nativeDict).length()) {
            wordHint = listOfListsOfLists.get(val).get(nativeDict).substring(0, wordLength + 1);
            twNativeWord.setText(wordHint);
            wordLength++;
        } else {
            showToast("Whole word is being shown ");
        }
    }

    public static String loadData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = sharedPreferences.getString(key, "");
        return text;
    }
    public static Integer loadData_lang(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Integer text = sharedPreferences.getInt(key,0);
        return text;
    }

    public int getInt(int size) {
        return rand.nextInt(size);
    }
    public int calcSmart(int size){
        int num = 0;
        ArrayList<Integer> wordsToChose = new ArrayList<Integer>();
        int summation = 0;
        int max = Integer.parseInt(listOfListsOfLists.get(0).get(4));
        for (int i = 0; i< size; i++){
            summation = summation + Integer.parseInt(listOfListsOfLists.get(i).get(4));
            if (Integer.parseInt(listOfListsOfLists.get(i).get(4)) > max) {
                max = Integer.parseInt(listOfListsOfLists.get(i).get(4));
            }
        }

        if (summation==0){
            num = rand.nextInt(size);
            return num;

        } else{
            for (int i = 0; i < size; i++){
                int count = max - Integer.parseInt(listOfListsOfLists.get(i).get(4));

                for (int j=0; j<count+1;j++){
                    wordsToChose.add(i);

                }
            }

            num = wordsToChose.get(rand.nextInt(wordsToChose.size()));
            return num;
        }
    }
    public int calc(int size) {
        int num = 0;
        if (listMintedProb.isEmpty()) {
            int min = Integer.parseInt(listOfListsOfLists.get(0).get(4));
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(listOfListsOfLists.get(i).get(4)) < min) {
                    min = Integer.parseInt(listOfListsOfLists.get(i).get(4));
                }
            }
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(listOfListsOfLists.get(i).get(4)) == min) {
                    listMintedProb.add(i);
                    num = i;
                }
            }
            listMintedProb.remove(listMintedProb.size() - 1);
        } else {
            int numOfElem = rand.nextInt(listMintedProb.size());
            num = listMintedProb.get(numOfElem);
            listMintedProb.remove(numOfElem);
        }
        return num;
    }

    public void nextWord(int dictSize) {
//        val = getInt(dictSize);
        val = calcSmart(dictSize);
        twNativeWord.setText("");
        editText.getText().clear();
        wordHint = String.valueOf(0);
        wordLength = 0;

        twForeignWord.setText(listOfListsOfLists.get(val).get(foreignDict));
        value = Integer.parseInt(listOfListsOfLists.get(val).get(2)) + 1;
        value_sum = Integer.parseInt(listOfListsOfLists.get(val).get(4)) + 1;
        sendData(val, 3, value,0);
        listOfListsOfLists.get(val).set(2, String.valueOf(value));
        listOfListsOfLists.get(val).set(4, String.valueOf(value_sum));
    }
    public class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urls, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonArray = response.optJSONArray("values");
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        List<String> listOfStrings = new ArrayList<String>();
                        try {
                            JSONArray json = jsonArray.optJSONArray(i);
                            strFWord = json.getString(0);
                            strNWord = json.getString(1);
                            if (json.length() == 2) {
                                json.put("0");
                                json.put("0");
                            } else if (json.length() == 3) {
                                json.put("0");
                            }
                            try {
                                countAppearance = json.getInt(2);
                            } catch (Exception e) {
                                countAppearance = 0;
                            }
                            try {
                                countRightAnswers = json.getInt(3);
                            } catch (Exception e) {
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
                    if (dictSize == 0){
                        Intent intent = new Intent(MainActivity.this, EditWord.class);
                        Bundle b = new Bundle();
                        b.putInt("row_num", 0);
                        b.putString("URL",urlSendData);
                        b.putInt("action",3);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
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

    private void sendData(int row, int col, int value_new, int actionChose) {
        this.row = row + 2;
        this.col = col;
        this.value = value_new;
        this.actionChose = actionChose;

        new SendMyData().execute();
    }

    public class SendMyData extends AsyncTask<String, Void, String> {
        int col = MainActivity.this.col;
        int row = MainActivity.this.row;
        int value = MainActivity.this.value;
        int choseAction = MainActivity.this.actionChose;
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(urlSendData);
//                        "https://script.google.com/macros/s/AKfycbwFAIvRwhMXr3VkLtsWgnJpODv7oQD5kruE1RSABnNrpi1H1qm6QZ-5qj6SE1F5ozzj7w/exec");
                //
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("values", value);
                postDataParams.put("shtID", sheetID_1);
                postDataParams.put("shtName", shtName);
                postDataParams.put("rowNum", row);
                postDataParams.put("colNum", col);
                postDataParams.put("actionChose", choseAction);
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

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
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
            while (itr.hasNext()) {

                String key = itr.next();
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

    public void switchLan() {
        foreignDict = (foreignDict - 1) * (-1);
        nativeDict = (foreignDict - 1) * (-1);
        twTextForeign.setText(listLangNames.get(foreignDict));
        twTextNative.setText(listLangNames.get(nativeDict));
        saveSheetIDAndName(MainActivity.this,foreignDict);
        nextWord(dictSize);
    }

    public static void saveSheetIDAndName(Context context,int langValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_lang, langValue);
        editor.apply();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }

}