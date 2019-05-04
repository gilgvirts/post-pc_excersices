package com.user.ex3;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Message>> chat;

    public MutableLiveData<ArrayList<Message>> getChat() {
        if (chat == null) {
            chat = new MutableLiveData<ArrayList<Message>>();
        }
        return chat;
    }
    public void setChat(ArrayList<Message> newChat){
        chat.postValue(newChat);
    }
}
