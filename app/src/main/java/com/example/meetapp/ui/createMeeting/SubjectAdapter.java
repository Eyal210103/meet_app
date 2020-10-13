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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    ArrayList<Subject> subjects;
    Context context;

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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("subjects.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void createArrayList(){
        this.subjects = new ArrayList<>();
//        try {
//            JSONObject obj = new JSONObject(loadJSONFromAsset());
//            JSONArray m_jArray = obj.getJSONArray("subjects");
//            for (int i = 0; i < m_jArray.length(); i++) {
//                JSONObject jo_inside = m_jArray.getJSONObject(i);
//                String name = jo_inside.getString("name");
//                int res = jo_inside.getInt("resource");
//                subjects.add(new Subject(res,name));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

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
