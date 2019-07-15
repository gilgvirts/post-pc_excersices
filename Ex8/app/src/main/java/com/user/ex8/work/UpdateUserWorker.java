package com.user.ex8.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.user.ex8.data.SetUserPrettyNameRequest;
import com.user.ex8.server.ServerHolder;
import com.user.ex8.server.UserManagementInterface;

import java.io.IOException;

import retrofit2.Response;

public class UpdateUserWorker extends Worker {
    public UpdateUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UserManagementInterface serverInterface = ServerHolder.getInstance().serverInterface;
        String token = getInputData().getString("token");
        token = "token " + token;
        String new_name = getInputData().getString("new_name");
        SetUserPrettyNameRequest request = new SetUserPrettyNameRequest();
        request.pretty_name = new_name;
        try {
            Response<SetUserPrettyNameRequest> response = serverInterface.updateUser(token ,request).execute();
            SetUserPrettyNameRequest info = response.body();
            String userAsJson = new Gson().toJson(info);
            Data outputData = new Data.Builder()
                    .putString("new_name", userAsJson)
                    .build();

            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}

