package com.user.ex8.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.user.ex8.data.UserResponse;
import com.user.ex8.server.ServerHolder;
import com.user.ex8.server.UserManagementInterface;

import java.io.IOException;

import retrofit2.Response;

public class UserInfoWorker extends Worker {

    public UserInfoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UserManagementInterface serverInterface = ServerHolder.getInstance().serverInterface;
        String token = getInputData().getString("token");
        token = "token " + token;
        String userName = getInputData().getString("key_user_name");
        Log.d("Worker", userName + " " + token);
        try {
            Response<UserResponse> response = serverInterface.retrieveUserInfo(token, userName).execute();
            UserResponse info = response.body();
            Log.d("response worker", response.toString());
            String userAsJson = new Gson().toJson(info);
            Data outputData = new Data.Builder()
                    .putString("user", userAsJson)
                    .build();
            Log.d("response worker", userAsJson);
            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}

