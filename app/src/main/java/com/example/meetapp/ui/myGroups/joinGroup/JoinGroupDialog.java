package com.example.meetapp.ui.myGroups.joinGroup;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.databinding.DialogJoinGroupBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinGroupDialog extends DialogFragment {

    JoinGroupDialogViewModel mViewModel;
    Group group;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(JoinGroupDialogViewModel.class);
        group = (Group) getArguments().getSerializable(Const.BUNDLE_GROUP_ID);
        mViewModel.init(group.getId());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogJoinGroupBinding binding = DialogJoinGroupBinding.inflate(inflater,container,false);
        View view =  binding.getRoot();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CircleImageView[] circleImageViews = new CircleImageView[3];
        circleImageViews[0] = binding.joinDialogImg1;
        circleImageViews[1] = binding.joinDialogImg2;
        circleImageViews[2] = binding.joinDialogImg3;


        Glide.with(requireActivity()).load(group.getPhotoUrl()).into(binding.joinGroupDialogCiv);
        binding.joinGroupDialogNameTextView.setText(group.getName());
        binding.joinGroupDialogJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.addUserToGroup();
                if (group.isPublic()) {
                    final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.myGroupsFragment);
                }else {
                    JoinGroupDialog.this.dismiss();
                    final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.RequestSentDialog);
                }
            }
        });
        
        mViewModel.getImgUrl().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                for (int i = 0; i < strings.size(); i++) {
                    Glide.with(requireActivity()).load(strings.get(i)).into(circleImageViews[i]);
                }
                if (strings.size()<circleImageViews.length) {
                    int diff = circleImageViews.length - strings.size();
                    for (int i = circleImageViews.length - 1; i < diff; i++) {
                        circleImageViews[i].setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        return view;
    }

}
