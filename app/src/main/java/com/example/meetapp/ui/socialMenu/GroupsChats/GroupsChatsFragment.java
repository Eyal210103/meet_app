package com.example.meetapp.ui.socialMenu.GroupsChats;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetapp.R;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.message.Message;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

public class GroupsChatsFragment extends Fragment {

    private GroupsChatsViewModel mViewModel;
    private MainActivityViewModel mainActivityViewModel;
    GroupChatsAdapter groupChatsAdapter;

    public static GroupsChatsFragment newInstance() {
        return new GroupsChatsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupsChatsViewModel.class);
        mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        mViewModel.init(mainActivityViewModel.getGroups().getValue());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_chats_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.group_chats_recycler_view);
        groupChatsAdapter = new GroupChatsAdapter(this, mViewModel.lastMessage, mViewModel.users, mainActivityViewModel.getGroups().getValue());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(groupChatsAdapter);

        mainActivityViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Group>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Group>> mutableLiveData) {
                groupChatsAdapter.notifyDataSetChanged();
            }
        });
        for (MutableLiveData<Group> m:mainActivityViewModel.getGroups().getValue()) {
            m.observe(getViewLifecycleOwner(), new Observer<Group>() {
                @Override
                public void onChanged(Group group) {
                    groupChatsAdapter.notifyDataSetChanged();
                }
            });
        }

        for (MutableLiveData<Message> m : mViewModel.lastMessage) {
            m.observe(getViewLifecycleOwner(), new Observer<Message>() {
                @Override
                public void onChanged(Message message) {
                    groupChatsAdapter.notifyDataSetChanged();
                }
            });
        }

        return view;
    }


}