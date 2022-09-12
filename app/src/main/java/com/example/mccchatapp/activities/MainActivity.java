package com.example.mccchatapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mccchatapp.R;
import com.example.mccchatapp.databinding.ActivityMainBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RootRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("MCC Chat App");



                preferenceManager = new PreferenceManager(getApplicationContext());

        getToken();
        setListener();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_create_groups_option)
        {
            RequestNewGroup();
        }
        return true;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, androidx.appcompat.R.style.AlertDialog_AppCompat);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e,g Coding Cafe");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", (dialogInterface, i) -> {
            String groupName = groupNameField.getText().toString();
            if (TextUtils.isEmpty(groupName))
            {
                Toast.makeText(MainActivity.this, "Please write Group Name..." ,Toast.LENGTH_SHORT).show();
            }
            else
            {
                CreateNewGroup(groupName);
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        RootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                Toast.makeText(MainActivity.this, groupName + "group a Created Successfully", Toast.LENGTH_SHORT).show();
            }
        });
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
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
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

