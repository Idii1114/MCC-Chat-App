package com.example.mccchatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mccchatapp.databinding.ActivitySignInBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {




    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();;

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;




    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        setListeners();

    }

    private void setListeners() {

        binding.buttonSignIn.setOnClickListener(view -> isValidSignInDetails());
        binding.textCreateNewAccount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.textForgotPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class)));


    }


    private void isValidSignInDetails() {

        String txt_email = binding.inputEmail.getText().toString();
        String txt_password = binding.inputPassword.getText().toString();

        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
            showToast("All fields are required!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()) {
            showToast("Enter a valid Email!");
        } else {
            signIn(txt_email, txt_password);
        }

    }




    private void signIn(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().getDocuments().size() > 0) {
                                            DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                                            preferenceManager.putString(Constants.KEY_PHONE_NUMBER, documentSnapshot.getString(Constants.KEY_PHONE_NUMBER));
                                            preferenceManager.putString(Constants.KEY_PASSWORD, password);
                                            preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 1);
                                            showToast("Welcome back!");
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        } else {
                            showToast("Email and password does not match!");
                        }
                    }
                });
    }


}