package com.example.meetapp.ui.settings;

import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    boolean isAccountFieldOpen;
    boolean isPrefFieldOpen;

    public SettingsViewModel() {
        isAccountFieldOpen = false;
        isPrefFieldOpen = false;
    }

}
