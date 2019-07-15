package com.user.ex8.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.user.ex8.data.SetImage;
import com.user.ex8.server.ServerHolder;
import com.user.ex8.server.UserManagementInterface;

import java.io.IOException;

import retrofit2.Response;

public class SetImageWorker extends Worker {
    public SetImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        UserManagementInterface serverInterface = ServerHolder.getInstance().serverInterface;
        String token = getInputData().getString("token");
        token = "token " + token;
        String new_url = getInputData().getString("img_url");
        SetImage request = new SetImage();
        request.imageUrl = new_url;
        try {
            Response<SetImage> response = serverInterface.setImage(token ,request).execute();
            SetImage info = response.body();
            String userAsJson = new Gson().toJson(info);
            Data outputData = new Data.Builder()
                    .putString("updated_user", userAsJson)
                    .build();
            return Result.success(outputData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}

