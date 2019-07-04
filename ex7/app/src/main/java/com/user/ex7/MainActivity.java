package com.user.ex7;

import android.Manifest.permission;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



public class MainActivity extends AppCompatActivity  implements TextWatcher{
    private static final String START_TEXT = "I'm going to call this number: ";
    private static final String PHONE_FIELD = "number_save";
    private static final String PREDEFINED_FIELD = "text_save";
    private static final String NOT_VALID_INPUT = "Missing information, please insert";
    private static final String APP_READY = "Thank you, stalking will start";
    private static final String CHANNEL_ID = "STALKER";

    private TextView phoneField;
    private TextView preTextField;
    private TextView informerView;
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
                permission.PROCESS_OUTGOING_CALLS,
                permission.SEND_SMS
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
        saveFields();

    }
    private void saveFields(){
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
        if(phoneField.getText().length() == 0 || (preTextField.getText().length() == 0)){
            informerView.setText(NOT_VALID_INPUT);

        }
        else{
            //Start Broadcast
            informerView.setText(APP_READY);
            saveFields();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }


}
