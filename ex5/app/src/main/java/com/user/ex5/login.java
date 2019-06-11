package com.user.ex5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button skip = findViewById(R.id.skip);
        skip.setOnClickListener(this);
        final Button conform = findViewById(R.id.conform);
        conform.setOnClickListener(this);
        conform.setEnabled(false);
        EditText userName = findViewById(R.id.userName);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    conform.setEnabled(false);
                } else {
                    conform.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                 // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
    @Override
    public void onClick(View v){
        int clicked = v.getId();
        String user = "default_user";
        if(clicked == R.id.conform){
            EditText userName = findViewById(R.id.userName);
            user = userName.getText().toString();
        }
        Intent returnIntent = getIntent();
        returnIntent.putExtra("user", user);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
