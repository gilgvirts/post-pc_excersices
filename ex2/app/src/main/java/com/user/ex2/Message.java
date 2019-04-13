package com.user.ex2;
import android.util.Log;

import java.util.Objects;
public class Message {
    final String text;
    final int id;
    public Message(int newId, String user_text){
        this.text = user_text;
        this.id = newId;

    }
    @Override
    public boolean equals(Object o) {
        Message M = (Message) o;
        return (this.id == M.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(text, id);
    }
}
