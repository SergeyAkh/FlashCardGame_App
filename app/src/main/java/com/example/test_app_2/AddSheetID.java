package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSheetID extends AppCompatActivity {
    private SharedPreferences pref;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY = "myKey";
    private static final String KEY_1 = "myKey_1";
    String sheetID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_item);
        EditText shtID = findViewById(R.id.txtSheetID);
        EditText shtName = findViewById(R.id.txtSheetName);
        Button buttonOK = findViewById(R.id.btnSheetID);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = shtID.getText().toString();
                String sheetName = shtName.getText().toString();
                if (path.isEmpty()|sheetName.isEmpty()){
                    Toast.makeText(view.getContext(), "Please enter ID of Sheet and Name", Toast.LENGTH_LONG).show();
                } else {
                    //loop for looking last "/" and therefore allocate SheetID
                    for (int i=39; i<=path.length();i++){
                        if (!path.substring(i,i+1).equals("/")) {
                            sheetID = path.substring(39,i+1);
                        }else{
                            break;
                        }
                    }
                    saveSheetIDAndName(view.getContext(), sheetID, sheetName);
                    Intent intent = new Intent(AddSheetID.this, MainActivity.class);
                    startActivity(intent);
                    finishAndRemoveTask();
                }
            }
        });
    }
    public static void saveSheetIDAndName(Context context,String text,String text_2) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, text);
        editor.putString(KEY_1, text_2);
        editor.apply();
    }

}