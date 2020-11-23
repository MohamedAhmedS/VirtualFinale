package com.example.virtualmeetingapp.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.virtualmeetingapp.models.Officer;
import com.example.virtualmeetingapp.models.Prisoner;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.models.Visitor;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.Global;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.example.virtualmeetingapp.utils.ToastHelper;

public abstract class BaseFragment extends Fragment {

    protected User admin;
    protected Officer officer;
    protected Visitor visitor;
    protected Prisoner prisoner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userType = new SystemPrefs().getUserType();
        switch (userType) {
            case Constants.USER_TYPE_OFFICER:
                officer = (Officer) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_PRISONER:
                prisoner = (Prisoner) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_VISITOR:
                visitor = (Visitor) Global.getCurrentUser();
                break;
            case Constants.USER_TYPE_ADMIN:
                admin = (User) Global.getCurrentUser();
                break;
            default:
                ToastHelper.showToast("Unauthorized Logged User ... Crashing HAHAHA :D");
                requireActivity().finish();
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public abstract void initXML(View view);

    public abstract void initVariables();
}
