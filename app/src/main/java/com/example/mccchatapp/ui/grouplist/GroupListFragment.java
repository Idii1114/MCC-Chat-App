package com.example.mccchatapp.ui.grouplist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mccchatapp.activities.UsersActivity;
import com.example.mccchatapp.databinding.FragmentGroupListBinding;

public class GroupListFragment extends Fragment {

    private FragmentGroupListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGroupListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setListener();

        return root;
    }

    private void setListener() {
        binding.btnSearch.setOnClickListener(view -> startActivity(new Intent(getContext().getApplicationContext(), UsersActivity.class)));
    }

}