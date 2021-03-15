package com.example.meetapp.ui.myGroups;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Consts;
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
        group = (Group) getArguments().getSerializable(Consts.BUNDLE_GROUP_ID);
        mViewModel.init(group.getId());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialog_join_group, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CircleImageView[] circleImageViews = new CircleImageView[3];
        circleImageViews[0] = view.findViewById(R.id.join_dialog_img_1);
        circleImageViews[1] = view.findViewById(R.id.join_dialog_img_2);
        circleImageViews[2] = view.findViewById(R.id.join_dialog_img_3);


        Glide.with(requireActivity()).load(group.getPhotoUrl()).into((CircleImageView)view.findViewById(R.id.join_group_dialog_civ));
        ((TextView)view.findViewById(R.id.join_group_dialog_name_textView)).setText(group.getName());
        view.findViewById(R.id.join_group_dialog_join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group.isPublic()) {
                    group.addUserToGroup();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.BUNDLE_GROUP_ID, group.getId());
                    final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.groupInfoFragment, bundle);
                }else {
                    group.requestToJoin();
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
            }
        });

        return view;
    }

}
