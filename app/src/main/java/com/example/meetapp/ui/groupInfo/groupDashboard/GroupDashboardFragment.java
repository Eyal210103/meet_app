package com.example.meetapp.ui.groupInfo.groupDashboard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meetapp.R;
import com.example.meetapp.model.User;
import com.example.meetapp.model.Message;
import com.example.meetapp.ui.groupInfo.GroupInfoFragment;

import java.util.ArrayList;

public class GroupDashboardFragment extends Fragment {

    private GroupDashboardViewModel mViewModel;
    GroupInfoFragment parent;
    TextView lastMessageTextView;
    DashMembersAdapter adapter;

    public static GroupDashboardFragment newInstance() {
        return new GroupDashboardFragment();
    }

    public void setParent(Fragment fragment){
        parent = (GroupInfoFragment) fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupDashboardViewModel.class);
        mViewModel.init((String) getArguments().get("id"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_dashboard_fragment, container, false);

        lastMessageTextView = view.findViewById(R.id.dash_last_message_textView);
        lastMessageTextView.setText(String.valueOf(R.string.no_messages));

        RecyclerView recyclerView = view.findViewById(R.id.group_dash_members_recycler);
        adapter = new DashMembersAdapter(this,mViewModel.getMembersMutableLiveData().getValue());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(requireActivity(),3);
        recyclerView.setLayoutManager(glm);

        View message = view.findViewById(R.id.include2);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.swipeToChat();
            }
        });

        View meeting = view.findViewById(R.id.include);
        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.swipeToMeetings();
            }
        });

        mViewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (MutableLiveData<User> u : mViewModel.getMembersMutableLiveData().getValue()) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        mViewModel.getLastMessage().observe(getViewLifecycleOwner(), new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                String m;
                if (message.getContext().length() > 15)
                    m = message.getSenderDisplayName() +":\n" + message.getContext().substring(0,15)+"...";
                else
                    m = message.getSenderDisplayName() +":\n" + message.getContext();
                lastMessageTextView.setText(m);
            }
        });
        return view;
    }
}