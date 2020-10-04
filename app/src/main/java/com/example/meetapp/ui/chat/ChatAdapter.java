package com.example.meetapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.message.Message;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> list;
    private Fragment context;

    public ChatAdapter(Fragment context, ArrayList<Message> list) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_adapter_receiver, parent, false);
            return new ChatViewHolderReceiver(v);
        }
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_adapter_sender, parent, false);
            return new ChatViewHolderSender(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        if (holder instanceof ChatViewHolderReceiver){
            ((ChatViewHolderReceiver) holder).context.setText(message.getContext());
            ((ChatViewHolderReceiver) holder).name.setText(message.getSenderDisplayName());
           // ((ChatViewHolderReceiver) holder).hour.setText(message.getHour());
        }
        if (holder instanceof ChatViewHolderSender){
            ((ChatViewHolderSender) holder).context.setText(message.getContext());
    //        ((ChatViewHolderSender) holder).hour.setText(message.getHour());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getSenderId().equals(CurrentUser.getInstance().getId())){
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatViewHolderSender extends RecyclerView.ViewHolder {
        TextView context, hour;
        public ChatViewHolderSender(@NonNull View itemView) {
            super(itemView);
            context = itemView.findViewById(R.id.sender_adapter_context_textView);
            hour = itemView.findViewById(R.id.sender_adapter_hour_textView);
        }
    }
    public class ChatViewHolderReceiver extends RecyclerView.ViewHolder {
        TextView name, context, hour;
        public ChatViewHolderReceiver(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.receiver_adapter_name_textView);
            context = itemView.findViewById(R.id.receiver_adapter_context_textView);
            hour =itemView.findViewById(R.id.receiver_adapter_hour_textView);
        }
    }
}
