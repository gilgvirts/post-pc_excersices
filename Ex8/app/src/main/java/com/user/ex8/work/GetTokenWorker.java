package com.user.ex8.work;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.user.ex8.data.TokenResponse;
import com.user.ex8.server.ServerHolder;
import com.user.ex8.server.UserManagementInterface;

import java.io.IOException;

import retrofit2.Response;

public class GetTokenWorker extends Worker {
        public GetTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            UserManagementInterface serverInterface = ServerHolder.getInstance().serverInterface;

            String userName = getInputData().getString("key_user_name");
            try {
                Response<TokenResponse> response = serverInterface.getToken(userName).execute();
                TokenResponse token = response.body();
                String userAsJson = new Gson().toJson(token);

                Data outputData = new Data.Builder()
                        .putString("token", userAsJson)
                        .build();

                return Result.success(outputData);

            } catch (IOException e) {
                e.printStackTrace();
                return Result.retry();
            }
        }
}
