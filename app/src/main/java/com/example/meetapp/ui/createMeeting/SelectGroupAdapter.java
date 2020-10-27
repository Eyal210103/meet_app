package com.example.meetapp.ui.createMeeting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectGroupAdapter extends RecyclerView.Adapter<SelectGroupAdapter.SelectGroupViewHolder> {
    ArrayList<MutableLiveData<Group>> groups;
    Context context;
    int selected;

    public SelectGroupAdapter(ArrayList<MutableLiveData<Group>> groups, Context context) {
        this.groups = groups;
        this.context = context;
        selected = -1;
    }

    @NonNull
    @Override
    public SelectGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_group_adapter,parent,false);
        return new SelectGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectGroupViewHolder holder, int position) {
        Group group = groups.get(position).getValue();
        if (group!= null){
            holder.textView.setText(group.getName());
            Glide.with(context).load(group.getPhotoUrl()).into(holder.circleImageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.main_app_design_selected));
                    selected = position;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public int getSelected(){
        return selected;
    }

    public class SelectGroupViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;

        public SelectGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.select_group_adapter_civ);
            textView = itemView.findViewById(R.id.select_group_adapter_group_name);
        }
    }
}
