package com.example.meetapp.ui.chat;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.meetapp.R;

public class GroupChatFragment extends Fragment {

    private GroupChatViewModel mViewModel;

    public static GroupChatFragment newInstance() {
        return new GroupChatFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupChatViewModel.class);
        mViewModel.init(getArguments().getString("id"));

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.group_chat_fragment, container, false);
        RecyclerView chatRecycle = view.findViewById(R.id.chat_recyclerView);
        EditText contextET = view.findViewById(R.id.chat_context_et);
        ImageView cameraIV = view.findViewById(R.id.chat_camera_iv);
        return view;
    }

}