package com.example.meetapp.ui.socialMenu.GroupsChats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatsAdapter extends RecyclerView.Adapter<GroupChatsAdapter.ChatsViewHolder> {

    ArrayList<Message> lastMessage;
    ArrayList<User> lastUser;
    ArrayList<MutableLiveData<Group>> groups;
    Context context;

    public GroupChatsAdapter(Context context, ArrayList<Message> lastMessage, ArrayList<User> lastUser, ArrayList<MutableLiveData<Group>> groups) {
        this.lastMessage = lastMessage;
        this.lastUser = lastUser;
        this.groups = groups;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupschats_adapter,parent,false);
        return new ChatsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        final Group curr = groups.get(position).getValue();
        Glide.with(context).load(curr.getPhotoUrl()).dontAnimate().into(holder.groupImgCiv);
        holder.groupName.setText(curr.getName());
        holder.lastMessage.setText(String.format("%s: %s", lastUser.get(position).getDisplayName(), lastMessage.get(position).getContext().substring(0,25)+"..."));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle =new Bundle();
                bundle.putString("groupId" , curr.getId());
                final NavController navController = Navigation.findNavController((Activity)context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_groupsChatsFragment_to_groupChatFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView groupImgCiv;
        TextView groupName;
        TextView lastMessage;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImgCiv = itemView.findViewById(R.id.group_image_chats_civ);
            groupName = itemView.findViewById(R.id.chat_adapter_group_name);
            lastMessage = itemView.findViewById(R.id.chat_adapter_last_message);
        }
    }
}
