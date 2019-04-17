package com.user.ex3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MessageDeleteConfirmation extends AppCompatActivity implements View.OnClickListener{
    final int delete_message = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_delete_confermation);
        Button yesDelete = findViewById(R.id.yes_button);
        Button noDelete = findViewById(R.id.no_button);
        yesDelete.setOnClickListener(this);
        noDelete.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        Intent returnIntent = getIntent();
        int decision = 0;
        if (view.getId() == R.id.yes_button){
                decision = delete_message;
        }
        returnIntent.putExtra("decision", decision);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
