package com.example.meetapp.ui.myGroups;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.Consts;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

public class JoinGroupFragment extends Fragment {

    private JoinGroupViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(JoinGroupViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.join_group_fragment, container, false);

        mViewModel.search("");
        EditText textResult = view.findViewById(R.id.join_group_search_editText);
        textResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mViewModel.search(textResult.getText().toString());
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.search(textResult.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.search(textResult.getText().toString());
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.join_group_search_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        GroupsAdapter adapter = new GroupsAdapter(this, mViewModel.getResult().getValue(), Consts.TYPE_JOIN_GROUP);
        recyclerView.setAdapter(adapter);

        mViewModel.getResult().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Group>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Group>> mutableLiveData) {
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}