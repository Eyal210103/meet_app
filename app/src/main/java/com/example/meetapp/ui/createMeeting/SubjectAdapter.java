package com.example.meetapp.ui.createMeeting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Subject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    ArrayList<Subject> subjects;
    Fragment context;
    int selected;

    public SubjectAdapter(Fragment context) {
        this.context = context;
        createArrayList();
        selected = -1;
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
        Glide.with(context.requireActivity()).load(subject.resource).into(holder.circleImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = position;
                OnClickInRecyclerView onClickInRecyclerView = (OnClickInRecyclerView)context;
                onClickInRecyclerView.onClickInRecyclerView(position, Const.ACTION_SUBJECT,0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public String getSelected(){
        return subjects.get(selected).name;
    }

    private void createArrayList(){
        this.subjects = new ArrayList<>();
        subjects.add(new Subject(R.drawable.restaurant, Const.SUBJECT_RESTAURANT));
        subjects.add(new Subject(R.drawable.basketball, Const.SUBJECT_BASKETBALL));
        subjects.add(new Subject(R.drawable.soccer, Const.SUBJECT_SOCCER));
        subjects.add(new Subject(R.drawable.football, Const.SUBJECT_FOOTBALL));
        subjects.add(new Subject(R.drawable.videogame, Const.SUBJECT_VIDEO_GAMES));
        subjects.add(new Subject(R.drawable.meetingicon, Const.SUBJECT_MEETING));
        subjects.add(new Subject(R.drawable.groupsicon , Const.SUBJECT_OTHER));
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circleImageView;
        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.subject_adapter_civ);
        }
    }
}
