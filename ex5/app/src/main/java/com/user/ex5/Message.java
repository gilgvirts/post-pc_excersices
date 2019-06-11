package com.user.ex5;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Objects;

public class Message implements Parcelable {
    final String text;
    final int id;
    final String timeStamp;

    public Message(int newId, String userText, String messageTimeStamp){
        this.id = newId;
        this.text = userText;
        this.timeStamp = messageTimeStamp;

    }
    public Message(Parcel in) {
        id = in.readInt();
        text = in.readString();
        timeStamp = in.readString();
    }
    public Message(HashMap hm){
        this.id = (int) hm.get("id");
        this.text = hm.get("text").toString();
        this.timeStamp = hm.get("ts").toString();
    }
    @Override
    public String toString() {
        return "Message{" + "id=" + id + "\\" + "text=" + text + "\\" + "time=" + timeStamp + "}";
    }

    @Override
    public boolean equals(Object o) {
        Message M = (Message) o;
        return (this.id == M.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(text, id, timeStamp);
    }

    public static HashMap toHashMap(Message m){
        HashMap hm = new HashMap<>();
        hm.put("id", m.id);
        hm.put("text", m.text);
        hm.put("ts", m.timeStamp);
        return hm;
    }

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(text);
    }
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
