package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
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

public class AddWords extends AppCompatActivity {
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    String sheetID_1, shtName, forWordToSend, netWordToSend;
    EditText foreignNewWord, nativeNewWord;
    ImageButton backToMain,submit;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);
        sheetID_1 = MainActivity.loadData(this,KEY);
        shtName = MainActivity.loadData(this,KEY_1);
        submit = findViewById(R.id.submitNewWords);
        backToMain = findViewById(R.id.goBack);
        foreignNewWord = findViewById(R.id.newForeignWord);
        nativeNewWord = findViewById(R.id.newNativeWord);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forWordToSend = foreignNewWord.getText().toString().trim();
                netWordToSend = nativeNewWord.getText().toString().trim();
                new SendRequest().execute();
                foreignNewWord.setText("");
                nativeNewWord.setText("");
            }
        });
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(AddWords.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}
        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("https://script.google.com/macros/s/AKfycbz05Dbq22BzdCaKPavv4d2Z-CZQhV0i3lkToFp8If-OuO-A1NRs05t5bPH6DZSvpEqu/exec");
                // https://script.google.com/macros/s/AKfycbxvoDuDu4-fahWIfEmpHXSN3Rh-l4juh7faGXYXEO7tpkI7zEBZHHPqIJDsGBaiOtMIqg/exec
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("foreignWord",forWordToSend);
                postDataParams.put("nativeWord",netWordToSend);
                postDataParams.put("shtID",sheetID_1);
                postDataParams.put("shtName",shtName);

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

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Word added",
                    Toast.LENGTH_SHORT).show();
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
}