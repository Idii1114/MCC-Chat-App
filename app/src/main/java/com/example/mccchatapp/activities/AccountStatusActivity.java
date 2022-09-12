package com.example.mccchatapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mccchatapp.databinding.ActivityAccountStatusBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountStatusActivity extends BaseActivity {

    private ActivityAccountStatusBinding binding;
    private DocumentReference documentReference;
    private PreferenceManager preferenceManager;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        setListeners();
        switchChecker();


    }

    private void setListeners() {

        binding.back.setOnClickListener(view -> onBackPressed());

    }

    private void switchChecker() {

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        binding.switchActive.setChecked(sharedPreferences.getBoolean("switchActive", false));
        binding.switchBusy.setChecked(sharedPreferences.getBoolean("switchBusy", false));
        binding.switchAway.setChecked(sharedPreferences.getBoolean("switchAway", false));


        binding.switchActive.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.switchActive.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchActive", true);
                editor.apply();
                documentReference.update(Constants.KEY_AVAILABILITY, 1);
                preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 1);
                binding.switchActive.setChecked(true);
                binding.switchBusy.setChecked(false);
                binding.switchAway.setChecked(false);
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchActive", false);
                editor.apply();
            }
        });

        binding.switchBusy.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.switchBusy.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchBusy", true);
                editor.apply();
                documentReference.update(Constants.KEY_AVAILABILITY, 2);
                preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 2);
                binding.switchBusy.setChecked(true);
                binding.switchActive.setChecked(false);
                binding.switchAway.setChecked(false);
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchBusy", false);
                editor.apply();
            }
        });

        binding.switchAway.setOnCheckedChangeListener((compoundButton, b) -> {
            if (binding.switchAway.isChecked()) {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchAway", true);
                editor.apply();
                documentReference.update(Constants.KEY_AVAILABILITY, 3);
                preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 3);
                binding.switchAway.setChecked(true);
                binding.switchActive.setChecked(false);
                binding.switchBusy.setChecked(false);
            } else {
                SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                editor.putBoolean("switchAway", false);
                editor.apply();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!binding.switchActive.isChecked() && !binding.switchBusy.isChecked() && !binding.switchAway.isChecked()) {
            documentReference.update(Constants.KEY_AVAILABILITY, 0);
            preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 0);
        }
    }
}
