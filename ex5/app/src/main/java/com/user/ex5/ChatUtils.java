package com.user.ex5;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatUtils {
    static class MessageCallback
            extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull Message p1, @NonNull Message p2) {
            return p1.equals(p2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message p1, @NonNull Message p2) {
            return p1.getText().equals(p2.getText()) && p1.getTimeStamp().equals(p2.getTimeStamp());
    }
    }
    interface MessageClickCallback {
        void onMessageLongClick(Message message);
    }
    static class MessageAdapter
            extends ListAdapter<Message, MessageHolder> {

        public MessageAdapter() {
            super(new MessageCallback());
        }

        public MessageClickCallback callback;

        @NonNull @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView =
                    LayoutInflater.from(context)
                            .inflate(R.layout.message, parent, false);
            final MessageHolder holder = new MessageHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m = getItem(holder.getAdapterPosition());
                    if (callback != null)
                        callback.onMessageLongClick(m);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MessageHolder MessageHolder, int position) {
            Message m = getItem(position);
            MessageHolder.text.setText(m.getText());
        }
    }



    static class MessageHolder
            extends RecyclerView.ViewHolder {
        public final TextView text;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.inp_message);
        }
    }
}
