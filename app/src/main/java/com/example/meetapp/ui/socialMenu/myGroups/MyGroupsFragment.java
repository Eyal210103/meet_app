package com.example.meetapp.ui.socialMenu.myGroups;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.meetapp.R;
import com.example.meetapp.dataLoadListener.DataUpdatedListener;

public class MyGroupsFragment extends Fragment implements DataUpdatedListener {

    private MyGroupsViewModel mViewModel;
    RecyclerView recyclerView;
    GroupsAdapter adapter;
    public static MyGroupsFragment newInstance() {
        return new MyGroupsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(this).get(MyGroupsViewModel.class);
        mViewModel.init(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_groups_fragment, container, false);

        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        adapter = new GroupsAdapter(this, mViewModel.getGroups().getValue());

        Button floatingActionButtonCreateGroup = view.findViewById(R.id.groups_create_group_fab);
        Button floatingActionButtonJoinGroup = view.findViewById(R.id.groups_join_group_fab);
        recyclerView = view.findViewById(R.id.groups_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        floatingActionButtonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_socialMenuFragment_to_createGroupFragment2);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDataUpdated() {
        try {
            adapter.notifyDataSetChanged();
        }catch (Exception ignored){

        }
    }
}

