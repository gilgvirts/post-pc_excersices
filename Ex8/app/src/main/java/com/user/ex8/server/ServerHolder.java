package com.user.ex8.server;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {
    private static ServerHolder instance = null;


    public synchronized static ServerHolder getInstance() {
        if (instance != null)
            return instance;

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://hujipostpc2019.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserManagementInterface serverInterface = retrofit.create(UserManagementInterface.class);
        instance = new ServerHolder(serverInterface);
        return instance;
    }


    public final UserManagementInterface serverInterface;

    private ServerHolder(UserManagementInterface serverInterface) {
        this.serverInterface = serverInterface;
    }
}

