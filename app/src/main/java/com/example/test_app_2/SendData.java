package com.example.test_app_2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class SendData extends AsyncTask<String, Void, String> {
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    Context context;
    void Helper(Context ctx){
        this.context = ctx;
    }
    String sheetID_1, shtName;
    MainActivity frame = new MainActivity();
    int col = frame.col;
    int row = frame.row;
    int value = frame.value;
    @Override
    protected String doInBackground(String... strings) {
        sheetID_1 = frame.loadData(context,KEY);
        shtName = frame.loadData(context,KEY_1);
        Log.d("my tag","row "+ row +" col "+col + " value "+value+ " shtID "+sheetID_1+" shtName "+shtName);
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
