package com.user.smalldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fish = (Button) findViewById(R.id.fish_button);
        fish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                go_fish(v);
            }
        });
        TextView inp_field = findViewById(R.id.int_field);
        inp_field.setText("Tap on me!");
        inp_field.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                go_inp(v);
            }
        });

    }
    public void go_fish(View v) {
        Intent intent = new Intent(this, Fish.class);
        startActivity(intent);
    }
    public void go_inp(View v) {
        Intent intent = new Intent(this, Inp.class);
        intent.putExtra("user_input", "");
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 0){
            if (resultCode == RESULT_OK){
                String inp = data.getStringExtra("user_input");
                TextView inp_field = findViewById(R.id.int_field);
                inp_field.setText("You have entered: " + inp);
            }
        }
    }
}
