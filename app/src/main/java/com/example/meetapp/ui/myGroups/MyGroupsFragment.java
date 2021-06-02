package com.example.meetapp.ui.myGroups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.databinding.MyGroupsFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

public class MyGroupsFragment extends Fragment implements OnClickInRecyclerView {

    MainActivityViewModel mViewModel;
    MyGroupsFragmentBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MyGroupsFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        GroupsAdapter adapter = new GroupsAdapter(this, mViewModel.getGroups().getValue(), Const.TYPE_MY_GROUPS);

        Button buttonCreateGroup = view.findViewById(R.id.groups_create_group_fab);
        Button buttonJoinGroup = view.findViewById(R.id.groups_join_group_fab);

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.groupsRecyclerView.setLayoutManager(llm);
        binding.groupsRecyclerView.setHasFixedSize(true);
        binding.groupsRecyclerView.setAdapter(adapter);

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_socialMenuFragment_to_createGroupFragment2);
            }
        });

        buttonJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_socialMenuFragment_to_joinGroupFragment);
            }
        });
        binding.groupsRecyclerView.setAdapter(adapter);

        mViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<Group>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<Group>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (LiveData<Group> m:mutableLiveData) {
                    if (!m.hasActiveObservers()) {
                        m.observe(getViewLifecycleOwner(), new Observer<Group>() {
                            @Override
                            public void onChanged(Group group) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onClickInRecyclerView(Object value, String action, Integer i) {
        if (action.equals(Const.ACTION_LEAVE)){
            String id = (String)value;
            mViewModel.leaveGroup(id);
        }
    }
}

