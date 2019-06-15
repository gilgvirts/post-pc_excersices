package com.user.ex5;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ChatUtils.MessageClickCallback {
    final int delete_message = 1;
    final String inp_id = "inp";
    final String chat_id = "disp";
    final String DEVICE_SAVE = "device";
    final String CHAT = "CHAT";
    final String TAG = "DB Work";
    final String SLASH = "\\";
    final String QUOTATION = "\"";
    final String CHAT_PATH = QUOTATION + "user_chats" + QUOTATION;
    final String USER_NAME_SAVE = "user_name";
    final String DEFAULT_USER = "default_user";
    final String USER_PATH = QUOTATION + "defaults" + QUOTATION + SLASH + QUOTATION + "user" + QUOTATION;
    final int DELETE_INTENT = 666;
    final int LOGIN_INTENT = 777;

    FirebaseFirestore db;
    String USER = DEFAULT_USER;
    String DB_USER = SLASH + QUOTATION + USER + QUOTATION;
    EditText textField;
    int serial_num = 1;
    RecyclerView recyclerView;
    String origin = "";

    private ChatUtils.MessageAdapter adapter
            = new ChatUtils.MessageAdapter();
    private ExecutorService load_executor;
    private ExecutorService save_executor;
    private ExecutorService delete_executor;

    private ArrayList<Message> chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        getOrigin(savedInstanceState);
        //try to get user
        getUser(savedInstanceState);
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

    private void getUser(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            USER = savedInstanceState.getString(USER_NAME_SAVE);
        }
        else {
            if (!loadUserName()) {
                ExecutorService load_name_executor = Executors.newCachedThreadPool();
                load_name_executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.collection(USER_PATH).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                                                                                    if (!queryDocumentSnapshot.getDocuments().isEmpty()) {
                                                                                        for (DocumentSnapshot doc : queryDocumentSnapshot.getDocuments()) {
                                                                                            if (doc.getData().get(USER_NAME_SAVE) != null) {
                                                                                                insertUser(doc.getData().get(USER_NAME_SAVE).toString());
                                                                                                Log.d("LoadName", "Name loaded");
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("LoadName", "Failed retrieving name");
                                                                                    }
                                                                                }
                                                                            }
                        );
                    }
                });
                load_name_executor.shutdown();
                while (!load_name_executor.isTerminated());
                if (USER.equals(DEFAULT_USER)) {
                    //open Login
                    Intent intent = new Intent(this, Login.class);
                    startActivityForResult(intent, LOGIN_INTENT);
                }
            }
        }
        if (!USER.equals(DEFAULT_USER)) {
            greetUser();
        }
    }

    private void getOrigin(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            origin = savedInstanceState.getString(DEVICE_SAVE);
        }
        else{
            origin = android.os.Build.MODEL;
        }
    }

    private void insertUser(String user) {
        USER = user;
    }

    private void greetUser() {
        getSupportActionBar().setTitle("Hello " + USER + "!");
    }

    @Override
    public void onMessageLongClick(Message message){
        Intent intent = new Intent(this, MessageDetails.class);
        intent.putExtra("to_delete", this.chat.indexOf(message));
        intent.putExtra("decision", 0);
        intent.putExtra("time_sent", message.getTimeStamp());
        intent.putExtra("origin", message.getOrigin());
        startActivityForResult(intent, DELETE_INTENT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE_INTENT) {

                int decision = data.getIntExtra("decision", 0);
                if (decision == delete_message) {
                    ArrayList<Message> chatCopy = new ArrayList<>(this.chat);
                    int toDel = data.getIntExtra("to_delete", -1);
                    if (toDel != -1) {
                        Message deletedMessage = chatCopy.get(toDel);
                        deleteMessage(deletedMessage.getId());
                        chatCopy.remove(toDel);
                        this.chat = chatCopy;
                        adapter.submitList(this.chat);
                        saveChat();
                        data.getIntExtra("to_delete", -1);
                    }
                }


            }
            if (requestCode == LOGIN_INTENT) {
                String temp = data.getStringExtra("user");
                if(!temp.equals(DEFAULT_USER)){
                    USER = temp;
                    greetUser();
                    //save new user name to db and sp
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_NAME_SAVE, USER);
                    editor.apply();
                    ExecutorService save_name_executor = Executors.newCachedThreadPool();
                    save_name_executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            HashMap user = new HashMap();
                            user.put(USER_NAME_SAVE, USER);
                            db.collection(USER_PATH)
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("User insert", "User name added");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("User insert", "Error adding user name", e);
                                        }
                                    });
                        }
                    });

                }
            }
        }
    }

    private void deleteMessage(final int deletedMessageId) {
        if(delete_executor == null){
            delete_executor = Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            });
        }
        delete_executor.execute(new Runnable() {
            @Override
            public void run() {
                db.collection(CHAT_PATH).document(Integer.toString(deletedMessageId)).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete", "Message successfully deleted!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Delete", "Error deleting Message", e);
                            }
                        });

            }
        });
        }



    @Override
    public void onClick(View view){
        String future_text = textField.getText().toString();
        if(future_text.isEmpty()){
            View mn = findViewById(R.id.main);
            String toUserMessage = "Empty Inputs Are Not Allowed. \nGo stand in the corner";
            int duration = Snackbar.LENGTH_SHORT;
            showSnackbar(mn, toUserMessage, duration);
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String timeStamp = simpleDateFormat.format(new Date());
        Message m = new Message(serial_num, future_text, timeStamp, origin);
        serial_num += 1;
        chat.add(m);
        saveMessageToDb(m);
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
        outState.putString(USER_NAME_SAVE, USER);
        outState.putString(DEVICE_SAVE, origin);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        saveChat();
        if (save_executor != null) {
            save_executor.shutdown();
        }
        if (load_executor != null) {
            load_executor.shutdown();
        }
        if (delete_executor != null) {
            delete_executor.shutdown();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        getChat();
    }
    public void saveChat(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chat);
        editor.putString(CHAT, json);
        editor.apply();
    }
    private boolean loadUserName(){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String userName = prefs.getString(USER_NAME_SAVE, null);
        if(userName != null){
            USER = userName;
            return true;
        }
        return false;
    }

    public void getChat(){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(CHAT, null);
        if (json != null){
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            chat = gson.fromJson(json, type);
        }
        loadChatFromDB();
    }
    private void loadChatFromDB() {
        if (load_executor == null) {
            load_executor = Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            });
        }
        load_executor.execute(new ActualLoad());
    }
    private void saveMessageToDb(Message m){
        if (save_executor == null) {
            save_executor = Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            });
        }
        save_executor.execute(new ActualSave(m));
    }
    class ActualSave implements Runnable {
        Message message;
        private ActualSave(Message m){
            this.message = m;
        }
        @Override
        public void run() {
                    db.collection(CHAT_PATH).document(Integer.toString(this.message.getId()))
                            .set(this.message);
        }
    }
    class ActualLoad implements Runnable{
            @Override
            public void run() {
                db.collection(CHAT_PATH)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                                                if (!queryDocumentSnapshot.getDocuments().isEmpty()) {
                                                    for (DocumentSnapshot doc : queryDocumentSnapshot.getDocuments()) {
                                                        if (doc.getData() != null) {
                                                            Gson gson = new Gson();
                                                            Message temp = doc.toObject(Message.class);
                                                            MergeToChat(temp);
                                                        }
                                                    }
                                                }
                                                else{
                                                    Log.d("LoadChat", "Failed loading messages");
                                                }

                            }
                        });
            }
    }
    void MergeToChat(Message m){
        int index = chat.indexOf(m);
            if (index == -1) {
                if(chat.size() > m.getSerial()) {
                    chat.add(m.getSerial(), m);
                }
                else {
                    chat.add(m);
                }
                if(m.getSerial() > this.serial_num){
                    serial_num = m.getSerial() + 1;
                }
            }
    }
}
