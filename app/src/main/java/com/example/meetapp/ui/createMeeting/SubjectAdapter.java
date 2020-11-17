package com.example.meetapp.ui.createMeeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Subject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    ArrayList<Subject> subjects;
    Context context;
    int selected;

    public SubjectAdapter(Context context) {
        this.context = context;
        createArrayList();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_adapter,parent,false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        Glide.with(context).load(subject.resource).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public String getSelected(){
        return "TRY";//subjects.get(selected).name;
    }

    private void createArrayList(){
        this.subjects = new ArrayList<>();
        subjects.add(new Subject(R.drawable.restaurant,"Restaurant"));
        subjects.add(new Subject(R.drawable.basketball,"Basketball"));
        subjects.add(new Subject(R.drawable.soccer,"Soccer"));
        subjects.add(new Subject(R.drawable.football,"Football"));
        subjects.add(new Subject(R.drawable.videogame,"Video Game"));
        subjects.add(new Subject(R.drawable.meetingicon,"Meeting"));
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circleImageView;
        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.subject_adapter_civ);
        }
    }
}
