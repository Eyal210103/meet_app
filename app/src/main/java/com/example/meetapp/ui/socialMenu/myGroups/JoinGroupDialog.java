package com.example.meetapp.ui.socialMenu.myGroups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinGroupDialog extends DialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_join_group, container, false);

        Group group = (Group) getArguments().getSerializable("group");
//        view.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Glide.with(requireActivity()).load(group.getPhotoUrl()).into((CircleImageView)view.findViewById(R.id.join_group_dialog_civ));
        ((TextView)view.findViewById(R.id.join_group_dialog_name_textView)).setText(group.getName());
        view.findViewById(R.id.join_group_dialog_join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.addUserToGroup();
                Bundle bundle = new Bundle();
                bundle.putString("group", group.getId());
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.groupInfoFragment, bundle);
            }
        });

        return view;
    }
}
