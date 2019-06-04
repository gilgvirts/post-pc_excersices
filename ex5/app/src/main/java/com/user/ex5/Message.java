package com.user.ex5;
import android.os.Parcelable;
import android.os.Parcel;
import java.util.Objects;

public class Message implements Parcelable {
    final String text;
    final int id;
    public Message(int newId, String user_text){
        this.text = user_text;
        this.id = newId;

    }
    private Message(Parcel in) {
        id = in.readInt();
        text = in.readString();
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

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(text);
    }
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
