package com.example.meetapp.ui.groupInfo.groupSettings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitingUsersAdapter extends RecyclerView.Adapter<WaitingUsersAdapter.WaitingUsersViewHolder> {

    private Fragment context;
    private ArrayList<MutableLiveData<User>> members;

    public WaitingUsersAdapter(Fragment context, ArrayList<MutableLiveData<User>> members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public WaitingUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_requests_adapter,parent,false);
        return new WaitingUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingUsersViewHolder holder, int position) {
        User user = members.get(position).getValue();
        if (members.get(position) != null && user != null) {
            Glide.with(context.requireActivity()).load(user.getProfileImageUrl()).into(holder.profile);
            holder.name.setText(user.getDisplayName());
            OnClickInRecyclerView onClickInRecyclerView = (OnClickInRecyclerView) context;
            holder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickInRecyclerView.onClickInRecyclerView(position, "Approve");
                    notifyItemRemoved(position);
                }
            });
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickInRecyclerView.onClickInRecyclerView(position, "Reject");
                    notifyItemRemoved(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class WaitingUsersViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile, approve , reject;
        TextView name;

        public WaitingUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.waiting_adapter_photo_civ);
            approve = itemView.findViewById(R.id.waiting_adapter_approve_civ);
            reject = itemView.findViewById(R.id.waiting_adapter_reject_civ);
            name = itemView.findViewById(R.id.waiting_adapter_name_textView);
        }
    }
}
