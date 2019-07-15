package com.user.ex8;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.Operation.State;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.user.ex8.data.TokenResponse;
import com.user.ex8.data.User;
import com.user.ex8.data.UserResponse;
import com.user.ex8.work.GetTokenWorker;
import com.user.ex8.work.SetImageWorker;
import com.user.ex8.work.UpdateUserWorker;
import com.user.ex8.work.UserInfoWorker;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button send_button;
    private TextView informerView;
    private TextView username_input;
    private static final String INVALID_USER = "Not valid username.";
    private static final String VALID_USER = "";
    private String USERNAME = "";
    private String TOKEN;
    private ProgressBar spinner;
    private User USER;
    private ImageView PROFILE_PIC;
    private Spinner picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PROFILE_PIC = findViewById(R.id.profilePic);
        PROFILE_PIC.setVisibility(View.GONE);
        send_button = findViewById(R.id.send);
        informerView = findViewById(R.id.informValid);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        picker = findViewById(R.id.imagePicker);
        picker.setVisibility(View.GONE);
        setTextListener();
        setOnClickSend();
    }

    private void setOnClickSend() {
        send_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                USERNAME = username_input.getText().toString();
                getToken(USERNAME);
            }
        });
    }


    private void loadImage(){
        if(USER == null){
            return;
        }
        Picasso.get().load("http://hujipostpc2019.pythonanywhere.com/" + USER.image_url).into(PROFILE_PIC);
        PROFILE_PIC.setVisibility(View.VISIBLE);
    }


    private void changeName(String name){
        TextView insert = findViewById(R.id.insert);
        insert.setText("Hello " + name + "!");
    }

    private void showUser() {
        if (USER == null){
            return;
        }
        if(USER.pretty_name == null){
            changeName(USERNAME);
        }
        else{
            changeName(USER.pretty_name);
        }
        send_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrettyName( username_input.getText().toString());
            }
        });
        send_button.setText("change your profile name");
        TextView view = findViewById(R.id.pickAColor);
        view.setVisibility(View.VISIBLE);

        informerView.setVisibility(View.GONE);
        TextView info = findViewById(R.id.info);
        info.setVisibility(View.GONE);
        String[] items = new String[]{"alien","crab", "unicorn", "robot", "octopus", "frog"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        picker.setAdapter(adapter);
        picker.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newUrl = "images/" + adapterView.getItemAtPosition(i).toString() + ".png";
                setImage(newUrl);
                USER.image_url = newUrl;
                loadImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        picker.setVisibility(View.VISIBLE);
        loadImage();
    }

    private void setTextListener() {
        username_input = findViewById(R.id.username);
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
                if (m.find() || str.isEmpty()) {
                    informerView.setText(INVALID_USER);
                    send_button.setEnabled(false);
                } else {
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

    //Workers code
    private void getToken(final String username) {
        //Set Loading screen
        spinner.setVisibility(View.VISIBLE);
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(GetTokenWorker.class)

                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_name", username).build())
                .addTag(workTagUniqueId.toString())
                .build();

        final Operation runningWork = WorkManager.getInstance(this).enqueue(checkConnectivityWork);
        runningWork.getState().observe(this, new Observer<State>() {
            @Override
            public void onChanged(Operation.State state) {
                if (state == null) return;
                if (state instanceof Operation.State.SUCCESS) {
                } else if (state instanceof Operation.State.FAILURE) {
                    // update UI - not connected :(
                }
            }
        });
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty())
                    return;
                WorkInfo info = workInfos.get(0);
                // now we can use it
                String token = info.getOutputData().getString("token");
                TokenResponse tokenResponse = new Gson().fromJson(token, TokenResponse.class);
                if(tokenResponse != null) {
                    TOKEN = tokenResponse.data;
                    getUserInfo(TOKEN);
                }
            }
        });
    }
    private void getUserInfo(String token){
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(UserInfoWorker.class)

                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_name", USERNAME).putString("token", token).build())
                .addTag(workTagUniqueId.toString())
                .build();

        final Operation runningWork = WorkManager.getInstance(this).enqueue(checkConnectivityWork);
        runningWork.getState().observe(this, new Observer<State>() {
            @Override
            public void onChanged(Operation.State state) {
                if (state == null) return;
                if (state instanceof Operation.State.SUCCESS) {
                    //Remove loading screen
                    spinner.setVisibility(View.GONE);
                } else if (state instanceof Operation.State.FAILURE) {
                    // update UI - not connected :(
                }
            }
        });
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty())
                    return;
                WorkInfo info = workInfos.get(0);
                if(!info.getOutputData().getKeyValueMap().isEmpty()) {
                    String user = info.getOutputData().getString("user");
                    UserResponse updatedData = new Gson().fromJson(user, UserResponse.class);
                    User updatedUser = updatedData.data;
                    if (updatedUser != null) {
                        USER = updatedUser;
                        showUser();
                    }
                }
            }
        });
    }
    private void setPrettyName(final String newName){
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(UpdateUserWorker.class)

                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("new_name", newName).putString("token", TOKEN).putString("key_user_name", USERNAME).build())
                .addTag(workTagUniqueId.toString())
                .build();

        final Operation runningWork = WorkManager.getInstance(this).enqueue(checkConnectivityWork);
        runningWork.getState().observe(this, new Observer<State>() {
            @Override
            public void onChanged(Operation.State state) {
                if (state == null) return;
                if (state instanceof Operation.State.SUCCESS) {
                } else if (state instanceof Operation.State.FAILURE) {
                    // update UI - not connected :(
                }
            }
        });
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty())
                    return;
                changeName(newName);
            }
        });

    }
    private void setImage(final String imageUrl){
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest checkConnectivityWork = new OneTimeWorkRequest.Builder(SetImageWorker.class)

                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("img_url", imageUrl).putString("token", TOKEN).putString("key_user_name", USERNAME).build())
                .addTag(workTagUniqueId.toString())
                .build();

        final Operation runningWork = WorkManager.getInstance(this).enqueue(checkConnectivityWork);
        runningWork.getState().observe(this, new Observer<State>() {
            @Override
            public void onChanged(Operation.State state) {
                if (state == null) return;
                if (state instanceof Operation.State.SUCCESS) {
                } else if (state instanceof Operation.State.FAILURE) {
                    // update UI - not connected :(
                }
            }
        });
        WorkManager.getInstance(this).getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty())
                    return;
            }
        });

    }
}
