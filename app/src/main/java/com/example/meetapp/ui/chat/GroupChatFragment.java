package com.example.meetapp.ui.chat;

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
import android.widget.EditText;
import android.widget.ImageView;

import com.example.meetapp.R;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.message.Message;

import java.util.ArrayList;

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

        final RecyclerView recyclerView = view.findViewById(R.id.chat_recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        final ChatAdapter adapter = new ChatAdapter(this,mViewModel.getMessages().getValue());
        recyclerView.setAdapter(adapter);

        final EditText contextET = view.findViewById(R.id.chat_context_et);
        ImageView cameraIV = view.findViewById(R.id.chat_camera_iv);

        view.findViewById(R.id.chat_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contextET.getText().toString().matches("")){
                    Message message = new Message();
                    message.setContext(contextET.getText().toString());
                    message.setSenderDisplayName(CurrentUser.getCurrentUser().getDisplayName());
                    message.setSenderId(CurrentUser.getCurrentUser().getId());
                    message.setDay(1);
                    message.setHour("12:00");
                    mViewModel.sendMessage(message);
                    contextET.setText("");
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            }
        });

        mViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(ArrayList<Message> messages) {
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        return view;
    }

}