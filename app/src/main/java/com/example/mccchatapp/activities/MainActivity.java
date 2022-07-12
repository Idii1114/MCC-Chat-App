package com.example.mccchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mccchatapp.R;
import com.example.mccchatapp.databinding.ActivityMainBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();


    private void showToast(String message){

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



                preferenceManager = new PreferenceManager(getApplicationContext());

        getToken();
        setListener();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    private void setListener() {

        binding.profileImage.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
        binding.addUser.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));

    }

    private Bitmap getUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {

        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token);

    }

    private void loadUserDetails() {
        binding.profileImage.setImageBitmap(getUserImage(preferenceManager.getString(Constants.KEY_IMAGE)));
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails();
    }

}

