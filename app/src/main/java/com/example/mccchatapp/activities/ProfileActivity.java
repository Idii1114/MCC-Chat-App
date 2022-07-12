package com.example.mccchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.example.mccchatapp.databinding.ActivityProfileBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();

    }

    private void setListeners() {

        binding.back.setOnClickListener(view -> onBackPressed());
        binding.buttonChangeDetails.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ChangeDetailsActivity.class)));
        binding.buttonAccountStatus.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AccountStatusActivity.class)));
        binding.buttonSendReport.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SendReportActivity.class)));
        binding.buttonSignOut.setOnClickListener(view -> signOut());

    }

    private void loadUserDetails() {

        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.profileImage.setImageBitmap(bitmap);
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.textPhone.setText(preferenceManager.getString(Constants.KEY_PHONE_NUMBER));

    }


    private void signOut() {
        showToast("Signed out successful!");
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(data)
                .addOnSuccessListener(unused -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener( e -> {
                    showToast("Unable to sign out!");
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails();
    }
}
