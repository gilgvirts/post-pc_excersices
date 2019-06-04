package com.user.smalldemo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class Inp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inp);
        EditText user_input = findViewById(R.id.editText);
        user_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInput(view);
                return false;
            }
        });

    }
    public void checkInput(View v){
        EditText user_input = (EditText) v;
        String inputValue = user_input.getText().toString();
        if(inputValue.length() == 5){
            Intent returnIntent = getIntent();
            returnIntent.putExtra("user_input", inputValue);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
