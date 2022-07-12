package com.example.mccchatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mccchatapp.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
    }

    private void setListener() {

        binding.back.setOnClickListener(view -> onBackPressed());
        binding.resetPassword.setOnClickListener(view -> isValidEmailDetails());

    }

    private void isValidEmailDetails() {
        String input_email = binding.inputEmail.getText().toString();
        if (input_email.isEmpty()){
            showToast("Email is required!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(input_email).matches()){
            showToast("Please provide a valid Email!");
        } else {
            resetPassword();
        }

    }

    private void resetPassword() {
        String input_email = binding.inputEmail.getText().toString();
        auth.sendPasswordResetEmail(input_email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Check your email to reset your password!");
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Try again! Something wrong happened!");
                        }
                    }
                });

    }
}