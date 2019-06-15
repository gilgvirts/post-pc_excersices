package com.user.ex5;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Objects;

public class Message implements Parcelable {
    int id;
    private int serial;
    private String text;
    private String timeStamp;
    private String messageOrigin;

    public Message(){}
    public Message(int serialNum, String userText, String messageTimeStamp, String origin){
        this.serial = serialNum;
        this.text = userText;
        this.timeStamp = messageTimeStamp;
        this.messageOrigin = origin;
        this.id = Objects.hash(serial, text, timeStamp, messageOrigin);

    }
    public Message(Parcel in) {
        serial = in.readInt();
        text = in.readString();
        timeStamp = in.readString();
        messageOrigin = in.readString();
    }
    public Message(HashMap hm){
        this.serial = (int) hm.get("serial");
        this.text = hm.get("text").toString();
        this.timeStamp = hm.get("ts").toString();
        this.messageOrigin = hm.get("origin").toString();
        this.id = Objects.hash(serial, text, timeStamp);
    }
    //Getters

    public int getId() {
        return id;
    }

    public int getSerial() {
        return serial;
    }

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getOrigin() {
        return messageOrigin;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        Message M = (Message) o;
        return (this.serial == M.serial);
    }
    @Override
    public int hashCode() {
        return Objects.hash(text, serial, timeStamp);
    }

    public static HashMap toHashMap(Message m){
        HashMap hm = new HashMap<>();
        hm.put("serial", m.getSerial());
        hm.put("text", m.getText());
        hm.put("ts", m.getTimeStamp());
        hm.put("origin", m.getOrigin());
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
