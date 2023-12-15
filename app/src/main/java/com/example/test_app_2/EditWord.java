package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
    String sheetID_1, shtName, forWordToSend, netWordToSend, oldForeignWord, oldNativeWord, value;
    EditText foreignNewWord, nativeNewWord;
    int row,col,actionChose;
    ImageButton backToMain;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);
        sheetID_1 = MainActivity.loadData(this,KEY);
        shtName = MainActivity.loadData(this,KEY_1);
        Button submit = findViewById(R.id.submitNewWords);
        backToMain = findViewById(R.id.goBack);
        foreignNewWord = findViewById(R.id.newForeignWord);
        nativeNewWord = findViewById(R.id.newNativeWord);
        Bundle b = getIntent().getExtras();
        if (b != null){
            row = b.getInt("row_num") + 2;
            oldForeignWord = b.getString("oldForeignWord");
            oldNativeWord = b.getString("oldNativeWord");
        }

        foreignNewWord.setText(oldForeignWord);
        nativeNewWord.setText(oldNativeWord);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forWordToSend = foreignNewWord.getText().toString().trim();
                netWordToSend = nativeNewWord.getText().toString().trim();
                if (!(forWordToSend.isEmpty())&!(netWordToSend.isEmpty())){
                    sendData(row,1,forWordToSend,0);
                    sendData(row,2,netWordToSend,0);
                    foreignNewWord.setText("");
                    nativeNewWord.setText("");
                    Toast.makeText(EditWord.this, "Word is being changed", Toast.LENGTH_LONG).show();
                }

                Toast.makeText(EditWord.this, "Please enter new value for word: "+oldForeignWord +" with value: "+oldNativeWord, Toast.LENGTH_LONG).show();

            }
        });
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(EditWord.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void sendData(int row, int col, String value_new,int actionChose) {
        this.row = row;
        this.col = col;
        this.value = value_new;
        this.actionChose = actionChose;
        new SendMyData().execute();
    }

    public class SendMyData extends AsyncTask<String, Void, String> {
        int col = EditWord.this.col;
        int row = EditWord.this.row;
        String value = EditWord.this.value;
        int actionChose = EditWord.this.actionChose;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://script.google.com/macros/s/AKfycbwtqhZb-aMYOAiR69ZdMXehyRfI8nS7stFRduU7JQdEQ0OTevvw_zyp4zPDgxi1EAq4uQ/exec");
//                        "https://script.google.com/macros/s/AKfycbwFAIvRwhMXr3VkLtsWgnJpODv7oQD5kruE1RSABnNrpi1H1qm6QZ-5qj6SE1F5ozzj7w/exec");
                //
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("values", value);
                postDataParams.put("shtID", sheetID_1);
                postDataParams.put("shtName", shtName);
                postDataParams.put("rowNum", row);
                postDataParams.put("colNum", col);
                postDataParams.put("actionChose", actionChose);

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