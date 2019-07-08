package com.user.ex8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
   private Button send_button;
   private TextView informerView;
    private static final String INVALID_USER = "Not valid username.";
    private static final String VALID_USER = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_button = findViewById(R.id.send);
        informerView = findViewById(R.id.informValid);
       // send_button.setEnabled(false);
        final TextView username_input = findViewById(R.id.username);
        username_input.addTextChangedListener((new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                Pattern p = Pattern.compile("[^a-zA-Z0-9]");
                Matcher m = p.matcher(str);
//                Log.d("String", str);
//                Log.d("matches", Boolean.toString(m.matches()));
                if( m.find()){
                    informerView.setText(INVALID_USER);
                    send_button.setEnabled(false);
                }
                else{
                    informerView.setText(VALID_USER);
                    send_button.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
               //Do nothing
            }
        }));
    }
}
