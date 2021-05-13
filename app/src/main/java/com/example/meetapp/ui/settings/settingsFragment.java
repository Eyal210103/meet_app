package com.example.meetapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.meetapp.callbacks.OnClickInFragment;
import com.example.meetapp.databinding.SettingsFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    View selected;

    public static settingsFragment newInstance() {
        return new settingsFragment();
    }

    View.OnClickListener onClickListenerOpenField = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selected == v) {
                v.setVisibility(View.GONE);
                selected = null;
            }else {
                v.setVisibility(View.VISIBLE);
                if (selected != null) {
                    selected.setVisibility(View.GONE);
                    selected = v;
                }
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SettingsFragmentBinding binding = SettingsFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        CircleImageView circleImageView = binding.settingsUserProfile;
        TextView textViewName = binding.settingsUserName;
        TextView textViewEmail = binding.settingsUserEmail;

        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).into(circleImageView);
        Glide.with(requireActivity()).load(CurrentUser.getInstance().getProfileImageUrl()).into(binding.settingsEditImgCiv);
        binding.settingsEditTextDisplayName.setText(CurrentUser.getInstance().getDisplayName());

        textViewName.setText(CurrentUser.getInstance().getDisplayName());
        textViewEmail.setText(CurrentUser.getInstance().getEmail());

        binding.settingsLogoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickInFragment onClickInFragment = (OnClickInFragment) requireActivity();
                onClickInFragment.onClickInFragment(Const.ACTION_LOGOUT);
            }
        });

        binding.settingsAccountTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected == binding.accountSettingsFieldLinearLayout) {
                    binding.accountSettingsFieldLinearLayout.setVisibility(View.GONE);
                    selected = null;
                }else {
                    binding.accountSettingsFieldLinearLayout.setVisibility(View.VISIBLE);
                    if (selected != null) {
                        selected.setVisibility(View.GONE);
                        selected = binding.accountSettingsFieldLinearLayout;
                    }
                }
            }
        });

        return view;
    }


}