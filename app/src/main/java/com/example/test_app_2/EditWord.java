package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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
import javax.net.ssl.HttpsURLConnection;

public class EditWord extends AppCompatActivity {
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    String sheetID_1, shtName, forWordToSend, netWordToSend, oldForeignWord, oldNativeWord, value,urlSendData,wordForeign,wordNative;
    EditText foreignNewWord, nativeNewWord;

    int row,col,actionChose,action;
    ImageButton backToMain,submit;
    TextView title;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);
        Log.d("my tag", "here");
        sheetID_1 = MainActivity.loadData(this,KEY);
        shtName = MainActivity.loadData(this,KEY_1);
        submit = findViewById(R.id.submitNewWords);
        backToMain = findViewById(R.id.goBack);
        foreignNewWord = findViewById(R.id.oldForeignWord);
        nativeNewWord = findViewById(R.id.oldNativeWord);
        title = findViewById (R.id.titleCard);
        Intent intent;
        intent = new Intent(EditWord.this, MainActivity.class);
        Bundle b = getIntent().getExtras();
        if (b != null){
            action = b.getInt("action");
            urlSendData = b.getString("URL");
            if (action == 0) {
                row = b.getInt("row_num") + 2;
                oldForeignWord = b.getString("oldForeignWord");
                oldNativeWord = b.getString("oldNativeWord");
                foreignNewWord.setText(oldForeignWord);
                nativeNewWord.setText(oldNativeWord);
                title.setText("Edit word");

            } else{
                title.setText("Enter new word \n to dictionary");
            }
        }
        foreignNewWord.setTextSize(20);
        nativeNewWord.setTextSize(20);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forWordToSend = foreignNewWord.getText().toString().trim();
                netWordToSend = nativeNewWord.getText().toString().trim();
                if (action==0){
                    //Action change word
                    if (!(forWordToSend.isEmpty())&!(netWordToSend.isEmpty())){
                        sendData(row,1,forWordToSend,action,forWordToSend,netWordToSend);
                        sendData(row,2,netWordToSend,action,forWordToSend,netWordToSend);
                        foreignNewWord.setText("");
                        nativeNewWord.setText("");
                        showToast("Word is being changed");
                        startActivity(intent);
                    } else {
                        showToast("Please enter new value for word: "+oldForeignWord +" with value: "+oldNativeWord);
                    }
                } else {
                    //Action add word
                    if (!(forWordToSend.isEmpty())&!(netWordToSend.isEmpty())){
                        sendData(row,1,forWordToSend,action,forWordToSend,netWordToSend);
                        foreignNewWord.setText("");
                        nativeNewWord.setText("");
                        showToast("Word is being added");

                    } else {
                        showToast("Please enter new word");

                    }
                }
            }
        });

    }
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }
    public void showToast(String txt) {
        View v = View.inflate(this, R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));
        TextView text = (TextView) v.findViewById(R.id.textForToast);
        text.setText(txt);
        Toast toast = new Toast(EditWord.this);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
    }
    private void sendData(int row, int col, String value_new,int actionChose,String wordForeign,String wordNative) {
        this.row = row;
        this.col = col;
        this.value = value_new;
        this.actionChose = actionChose;
        this.wordForeign = wordForeign;
        this.wordNative = wordNative;
        new SendMyData().execute();
    }

    public class SendMyData extends AsyncTask<String, Void, String> {
        int col = EditWord.this.col;
        int row = EditWord.this.row;
        String value = EditWord.this.value;
        int actionChose = EditWord.this.actionChose;
        String wordForeign = EditWord.this.wordForeign;
        String wordNative = EditWord.this.wordNative;

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
                postDataParams.put("actionChose", actionChose);
                postDataParams.put("foreignWord", wordForeign);
                postDataParams.put("nativeWord", wordNative);

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
}