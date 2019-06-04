package com.user.ex4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ChatUtils.MessageClickCallback {
    FirebaseFirestore db;
    final String SLASH = "\\";
    final String 
    String USER = SLASH + "\"default_user\"";
    EditText textField;
    int id_num = 1;
    final int delete_message = 1;
    final String inp_id = "inp";
    final String chat_id = "disp";
    static final String CHAT = "CHAT";
    final int delete_request_number = 666;
    final String TAG = "DB Work";
    private ChatUtils.MessageAdapter adapter
            = new ChatUtils.MessageAdapter();
    final String fire_base_chat = "\"chats\"";
    private ExecutorService load_executor;
    private ExecutorService save_executor;

    private ArrayList<Message> chat;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (chat == null) {
            chat = new ArrayList<>();
        }
        try {
            getChat();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    saveChat();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timeStamp = simpleDateFormat.format(new Date());
        Message m = new Message(id_num, future_text, timeStamp);
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
        saveChat();

    }
    @Override
    public void onStop() {
        super.onStop();
        if (save_executor != null) {
            save_executor.shutdown();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        //loadChat();
    }
    public void saveChat(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chat);
        saveChatToDb(json);
        editor.putString(CHAT, json);
        editor.apply();
    }
    public void getChat(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(CHAT, null);
        if (json != null){
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            chat = gson.fromJson(json, type);
        }
        loadChatFromDB();
    }
    private void loadChatFromDB() {
        load_executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }});
        load_executor.execute(new ActualLoad());
    }
    private void saveChatToDb(String json){
        save_executor = Executors.newSingleThreadExecutor();
        save_executor.execute(new ActualSave(json));
    }
    class ActualSave implements Runnable {
        String chat;
        public ActualSave(String json){
            this.chat = json;
        }
        @Override
        public void run() {
            if (chat != null) {
                    db.collection(fire_base_chat + USER)
                            .add(this.chat)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

            }
        }
    }
    class ActualLoad implements Runnable{
            @Override
            public void run() {
                db.collection(fire_base_chat + USER)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                                Gson gson = new Gson();
                                String json = queryDocumentSnapshot.getDocuments().get(0).getData().toString();
                                Type type = new TypeToken<ArrayList<Message>>() {}.getType();
                                ArrayList<Message> temp = gson.fromJson(json, type);
//                                Message m = new Message((HashMap));
                                MergeToChat(temp);
                            }
                        });
            }
    }
    void MergeToChat(ArrayList<Message> temp){
        for (Message m: temp) {
            int index = chat.indexOf(m);
            if (index == -1) {
                chat.add(m);
            }
        }

    }
}
