package com.example.meetapp.ui.myGroups;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

public class MyGroupsFragment extends Fragment implements OnClickInRecyclerView {

    MainActivityViewModel mViewModel;
    RecyclerView recyclerView;
    GroupsAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_groups_fragment, container, false);

        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        adapter = new GroupsAdapter(this, mViewModel.getGroups().getValue(), Const.TYPE_MY_GROUPS);

        Button buttonCreateGroup = view.findViewById(R.id.groups_create_group_fab);
        Button buttonJoinGroup = view.findViewById(R.id.groups_join_group_fab);

        recyclerView = view.findViewById(R.id.groups_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

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
        recyclerView.setAdapter(adapter);

        mViewModel.getGroups().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Group>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Group>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (MutableLiveData<Group> m:mutableLiveData) {
                    m.observe(getViewLifecycleOwner(), new Observer<Group>() {
                        @Override
                        public void onChanged(Group group) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        return view;
    }


    @Override
    public void onClickInRecyclerView(Object value, String action, int i) {
        if (action.equals(Const.ACTION_LEAVE)){
            String id = (String)value;
            mViewModel.leaveGroup(id);
        }
    }
}

