package com.user.ex2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.support.design.widget.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText textField;
    int id_num = 1;
    final String inp_id = "inp";
    final String chat_id = "disp";
    private ChatUtils.MessageAdapter adapter
            = new ChatUtils.MessageAdapter();

    private ArrayList<Message> chat = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textField = findViewById(R.id.input_insert);
        Button send = findViewById(R.id.input_send);
        recyclerView = findViewById(R.id.chat_recycler);
        send.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.submitList(chat);
        if (savedInstanceState!= null){
            if(savedInstanceState.get(inp_id) != null){
                String inp = savedInstanceState.getString(inp_id);
                textField.setText(inp);
            }
            if(savedInstanceState.get(chat_id) != null){
                chat = (ArrayList<Message>)savedInstanceState.getSerializable(chat_id);
                adapter.submitList(chat);
            }
        }

    }
    @Override
    public void onClick(View view){
        String future_text = textField.getText().toString();
        if(future_text.isEmpty()){
            View m = findViewById(R.id.main);
            String message = "No Empty inputs MR!";
            int duration = Snackbar.LENGTH_SHORT;
            showSnackbar(m, message, duration);
            return;
        }
        Message m = new Message(id_num, future_text);
        id_num += 1;
        chat.add(m);
        Log.d("chat", chat.toString());
        adapter.submitList(chat);
        adapter.notifyDataSetChanged();
        textField.setText("");
        show_children(recyclerView);
    }
    public void showSnackbar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }
    private void show_children(View v) {
        ViewGroup viewgroup = (ViewGroup) v;
        Log.d("items in activity: ", Integer.toString(viewgroup.getChildCount()));
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(inp_id,  textField.getText().toString());
        outState.putSerializable(chat_id, chat);
    }

}