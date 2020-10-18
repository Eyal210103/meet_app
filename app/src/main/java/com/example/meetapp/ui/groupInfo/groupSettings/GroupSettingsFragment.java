package com.example.meetapp.ui.groupInfo.groupSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupSettingsFragment extends Fragment implements OnClickInRecyclerView {

    private GroupSettingsViewModel mViewModel;

    public static GroupSettingsFragment newInstance() {
        return new GroupSettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);
        mViewModel.init(getArguments().getString("id"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.group_setting_fragment, container, false);

        WaitingUsersAdapter adapter = new WaitingUsersAdapter(this,mViewModel.getPaddingUsers().getValue());
        RecyclerView recyclerView = view.findViewById(R.id.group_settings_waiting_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        mViewModel.getPaddingUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (MutableLiveData<User> u :mutableLiveData) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onClickInRecyclerView(Object value, String action) {
        if (action.equals("Approve")){
            mViewModel.approveUser((Integer)value);
        }else if(action.equals("Reject")){
            mViewModel.rejectUser((Integer)value);
        }
    }
}