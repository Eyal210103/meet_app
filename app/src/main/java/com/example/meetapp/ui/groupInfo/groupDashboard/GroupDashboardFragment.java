package com.example.meetapp.ui.groupInfo.groupDashboard;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.meetapp.R;
import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.groupInfo.GroupInfoFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoViewModel;

import java.util.ArrayList;

public class GroupDashboardFragment extends Fragment {

    private GroupDashboardViewModel mViewModel;
    private GroupInfoViewModel parentViewModel;
    private GroupInfoFragment parent;
    private MainActivityViewModel mainActivityViewModel;
    TextView lastMessageTextView;
    DashMembersAdapter adapter;

    public static GroupDashboardFragment newInstance() {
        return new GroupDashboardFragment();
    }

    public void setParent(Fragment fragment){
        this.parent = (GroupInfoFragment) fragment;
        parentViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupDashboardViewModel.class);
        mViewModel.init(parentViewModel.getGroupMutableLiveData().getValue().getId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_dashboard_fragment, container, false);

        lastMessageTextView = view.findViewById(R.id.dash_last_message_textView);
        lastMessageTextView.setText("There is no messages");

        RecyclerView recyclerView = view.findViewById(R.id.group_dash_members_recycler);
        adapter = new DashMembersAdapter(this,parentViewModel.getMembersMutableLiveData().getValue());
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

        parentViewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (MutableLiveData<User> u : parentViewModel.getMembersMutableLiveData().getValue()) {
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