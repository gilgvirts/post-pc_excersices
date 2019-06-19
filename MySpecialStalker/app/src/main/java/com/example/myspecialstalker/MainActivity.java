package com.example.myspecialstalker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import java.security.Permission;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity  implements TextWatcher{
    private static final String START_TEXT = "I'm going to call this number: ";
    private static final String PHONE_FIELD = "number_save";
    private static final String PREDEFINED_FIELD = "text_save";
    private static final String NOT_VALID_INPUT = "Missing information, please insert";
    private static final String APP_READY = "Thank you, stalking will start";

    private TextView phoneField;
    private TextView preTextField;
    private TextView informerView;
    private boolean permissionsGiven = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermission();
        setContentView(R.layout.activity_main);
        //try loading from SP
        loadFields();

    }

    private void loadFields() {
        phoneField = findViewById(R.id.PhoneInsert);
        preTextField = findViewById(R.id.PreTextInsert);
        informerView = findViewById(R.id.informer);
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        phoneField.setText(prefs.getString(PHONE_FIELD, ""));
        preTextField.setText(prefs.getString(PREDEFINED_FIELD, START_TEXT));
        phoneField.addTextChangedListener(this);
        preTextField.addTextChangedListener(this);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
//        Log.d("Permissions", permissions + " result);
        if (grantResults.length > 0){
            for (int res: grantResults){
                if(res == PackageManager.PERMISSION_DENIED){
                    askPermission();
                }
            }
        }
    }

    private void askPermission() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_PHONE_STATE
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PHONE_FIELD, phoneField.getText().toString());
        editor.putString(PREDEFINED_FIELD, preTextField.getText().toString());
        editor.apply();
    }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d("Text changes", "preText: " + preTextField.getText() + " phoneField:" + phoneField.getText().length());
            if(phoneField.getText().length() == 0 || (preTextField.getText().length() == 0)){
                informerView.setText(NOT_VALID_INPUT);

            }
            else{
                //Start Broadcast
                informerView.setText(APP_READY);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
//    Sending sms https://developer.android.com/guide/components/intents-common#java
