package com.example.meetapp.ui.chat;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MutableLiveData<Message>> list;
    private Fragment context;

    public ChatAdapter(Fragment context, ArrayList<MutableLiveData<Message>> list) {
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
        Message message = list.get(position).getValue();
        if (message != null) {
            long millis = message.getTime();
            @SuppressLint("DefaultLocale")
            String hour = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis));

            if (holder instanceof ChatViewHolderReceiver) {
                ((ChatViewHolderReceiver) holder).context.setText(message.getContext());
                ((ChatViewHolderReceiver) holder).name.setText(message.getSenderDisplayName());
                if (message.getUrl() != null) {
                    ((ChatViewHolderReceiver) holder).image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getUrl()).into(((ChatViewHolderReceiver) holder).image);
                }
                ((ChatViewHolderReceiver) holder).hour.setText(hour);
            }
            if (holder instanceof ChatViewHolderSender) {
                ((ChatViewHolderSender) holder).context.setText(message.getContext());
                ((ChatViewHolderSender) holder).hour.setText(hour);
                if (message.getUrl() != null) {
                    ((ChatViewHolderSender) holder).image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getUrl()).into(((ChatViewHolderSender) holder).image);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (list.get(position).getValue().getSenderId().equals(CurrentUser.getInstance().getId())){
                return 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatViewHolderSender extends RecyclerView.ViewHolder {
        TextView context, hour;
        ImageView image;
        public ChatViewHolderSender(@NonNull View itemView) {
            super(itemView);
            context = itemView.findViewById(R.id.sender_adapter_context_textView);
            hour = itemView.findViewById(R.id.sender_adapter_hour_textView);
            image = itemView.findViewById(R.id.sender_imageView);
        }
    }
    public class ChatViewHolderReceiver extends RecyclerView.ViewHolder {
        TextView name, context, hour;
        ImageView image;
        public ChatViewHolderReceiver(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.receiver_adapter_name_textView);
            context = itemView.findViewById(R.id.receiver_adapter_context_textView);
            hour =itemView.findViewById(R.id.receiver_adapter_hour_textView);
            image = itemView.findViewById(R.id.receiver_imageView);
        }
    }
}
