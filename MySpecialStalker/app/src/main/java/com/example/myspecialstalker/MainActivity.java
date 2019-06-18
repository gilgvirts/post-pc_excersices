package com.example.myspecialstalker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    private static final String START_TEXT = "I'm going to call this number: ";
    private static final String PHONE_FIELD = "number_save";
    private static final String PREDEFINED_FIELD = "text_save";
    private static final String NOT_VALID_INPUT = "Missing information, please insert";
    private static final String APP_READY = "Thank you, stalking will start";
    private static final int REQUEST_SMS_PERMISSION_CODE = 1546;
    private static final int REQUEST_PHONE_PERMISSION_CODE = 1547;

    private TextView phoneField;
    private TextView preTextField;
    private TextView informerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        askPermission(0);
        super.onCreate(savedInstanceState);
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
        phoneField.addTextChangedListener(new InputVerifier());
        preTextField.addTextChangedListener(new InputVerifier());


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("Permissions", permissions + " results: " + Integer.toString(grantResults));
        if (grantResults.length > 0
                && !(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            switch (requestCode) {
                case REQUEST_SMS_PERMISSION_CODE:
                    askPermission(0);
                case REQUEST_PHONE_PERMISSION_CODE:
                    askPermission(1);
            }
        }
        else if(requestCode == REQUEST_SMS_PERMISSION_CODE) {
            askPermission(1);
        }
    }

    private void askPermission(int permission) {
        switch (permission) {
            case 0:
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.SEND_SMS},
                        REQUEST_SMS_PERMISSION_CODE);
            case 1:
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.PROCESS_OUTGOING_CALLS},
                        REQUEST_PHONE_PERMISSION_CODE);
        }
    }
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
    class InputVerifier implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d("Text changes", "preText: " + preTextField.getText() + " phoneField:" + phoneField.getText());
            if(!phoneField.getText().equals("") && !preTextField.getText().equals("")){
                informerView.setText(APP_READY);
                //Start Broadcast
            }
            else{
                informerView.setText(NOT_VALID_INPUT);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}