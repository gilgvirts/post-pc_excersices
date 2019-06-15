package com.user.ex5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetails extends AppCompatActivity implements View.OnClickListener{
    final int delete_message = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_delete_confermation);
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this);
        Intent callingIntent = getIntent();
        TextView det = findViewById(R.id.details);
        det.setText("Message details:\n" + "Sent on: " + callingIntent.getStringExtra("time_sent")
                + "\n From: " + callingIntent.getStringExtra("origin"));


    }
    @Override
    public void onClick(View view){
        Intent returnIntent = getIntent();
        returnIntent.putExtra("decision", delete_message);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
