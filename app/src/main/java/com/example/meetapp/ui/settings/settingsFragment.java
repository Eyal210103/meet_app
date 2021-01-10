package com.example.meetapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.model.CurrentUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    public static settingsFragment newInstance() {
        return new settingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.settings_fragment, container, false);
        CircleImageView circleImageView = view.findViewById(R.id.settings_user_profile);
        TextView textViewName = view.findViewById(R.id.settings_user_name);
        TextView textViewEmail = view.findViewById(R.id.settings_user_email);

        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).into(circleImageView);
        textViewName.setText(CurrentUser.getInstance().getDisplayName());
        textViewEmail.setText(CurrentUser.getInstance().getEmail());
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickInFragment onClickInFragment = (OnClickInFragment) requireActivity();
                onClickInFragment.onClickInFragment(getString(R.string.logoutAction));
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

}