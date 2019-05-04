package com.user.ex3;

import android.content.Intent;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ChatUtils.MessageClickCallback {
    EditText textField;
    int id_num = 1;
    final int delete_message = 1;
    final String inp_id = "inp";
    final String chat_id = "disp";
    final int delete_request_number = 666;
    private ChatUtils.MessageAdapter adapter
            = new ChatUtils.MessageAdapter();

    private ArrayList<Message> chat;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chat =  new ArrayList<>();
        setContentView(R.layout.activity_main);
        textField = findViewById(R.id.input_insert);
        Button send = findViewById(R.id.input_send);
        recyclerView = findViewById(R.id.chat_recycler);
        send.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.submitList(chat);
        adapter.callback = this;
        if (savedInstanceState!= null){
            if(savedInstanceState.get(inp_id) != null){
                String inp = savedInstanceState.getString(inp_id);
                textField.setText(inp);
            }
            if(savedInstanceState.get(chat_id) != null){
                chat = savedInstanceState.getParcelableArrayList(chat_id);
                adapter.submitList(chat);
            }
        }
        Log.d("chat size", Integer.toString(chat.size()));

    }
    @Override
    public void onMessageLongClick(Message message){
        Intent intent = new Intent(this, MessageDeleteConfirmation.class);
        intent.putExtra("to_delete", this.chat.indexOf(message));
        startActivityForResult(intent, delete_request_number);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == delete_request_number){
            if (resultCode == RESULT_OK){
                int decision = data.getIntExtra("decision", 0);
                if (decision == delete_message){
                    ArrayList<Message> chatCopy = new ArrayList<>(this.chat);
                    int toDel = data.getIntExtra("to_delete", -1);
                    if(toDel != -1){
                        chatCopy.remove(toDel);
                    }
                    this.chat = chatCopy;
                    adapter.submitList(this.chat);
                }


            }
        }
    }
    @Override
    public void onClick(View view){
        String future_text = textField.getText().toString();
        if(future_text.isEmpty()){
            View m = findViewById(R.id.main);
            String message = "Empty Inputs Are Not Allowed. Go stand in the corner";
            int duration = Snackbar.LENGTH_SHORT;
            showSnackbar(m, message, duration);
            return;
        }
        Message m = new Message(id_num, future_text);
        id_num += 1;
        chat.add(m);
        adapter.submitList(chat);
        adapter.notifyDataSetChanged();
        textField.setText("");
    }
    public void showSnackbar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(inp_id,  textField.getText().toString());
        outState.putParcelableArrayList(chat_id, chat);
    }

}