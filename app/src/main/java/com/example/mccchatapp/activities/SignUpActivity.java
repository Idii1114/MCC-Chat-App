package com.example.mccchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mccchatapp.databinding.ActivitySignUpBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private String encodedImage;
    private String userId;

    private Uri imageUri;

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();

    }

    private void setListeners() {

        binding.back.setOnClickListener(view -> onBackPressed());

        binding.profileImage.setOnClickListener(view -> choosePicture());

        binding.buttonSignUp.setOnClickListener(view -> isValidSignUpDetails());

        binding.checkBox.setOnClickListener(view -> {
            if (binding.checkBox.isChecked()) {
                binding.buttonSignUp.setVisibility(View.VISIBLE);
            } else {
                binding.buttonSignUp.setVisibility(View.GONE);
            }
        });

    }

    private void isValidSignUpDetails() {
        String input_name = binding.inputName.getText().toString();
        String input_email = binding.inputEmail.getText().toString();
        String input_phone = binding.inputPhoneNumber.getText().toString();
        String input_password = binding.inputPassword.getText().toString();
        String input_password2 = binding.inputConfirmPassword.getText().toString();
        String firstNum = input_phone.substring(0, 1);
        String secondNum = input_phone.substring(1, 2);

        if (TextUtils.isEmpty(input_name) || TextUtils.isEmpty(input_email) || TextUtils.isEmpty(input_phone)
                || TextUtils.isEmpty(input_password) || TextUtils.isEmpty(input_password2)) {
            showToast("All fields are required!");
        } else if (input_phone.length() != 11 || !firstNum.matches("0") || !secondNum.matches("9")
                || !Pattern.matches("[0-9]+", input_phone)) {
            showToast("Invalid Phone Number");
        } else if (input_password.length() <6) {
            showToast("Password must be at least 6 characters or above!");
        } else if (!input_password.equals(input_password2)){
            showToast("Password and Confirm Password are not the same!");
        } else if (encodedImage == null) {
            binding.textUploadImage.setVisibility(View.VISIBLE);
            showToast("Select a Profile Image!");
        } else {
            signUp(input_name, input_email, input_phone,  input_password);
        }
    }

    private void choosePicture() {

        binding.textUploadImage.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageUri = intent.getData();
        binding.profileImage.setImageURI(imageUri);
        pickImage.launch(intent);
    }

    private void signUp(final String name, String email, String phone, String password) {

        HashMap<String, Object> user = new HashMap<>();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        user.put(Constants.KEY_NAME, name);
                        user.put(Constants.KEY_PHONE_NUMBER, phone);
                        user.put(Constants.KEY_EMAIL, email);
                        user.put(Constants.KEY_IMAGE, encodedImage);
                        if(task.isSuccessful()){
                            showToast("Congratulations you are now registered!");
                            userId = auth.getCurrentUser().getUid();
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(documentReference -> {
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, userId);
                                        preferenceManager.putString(Constants.KEY_NAME, name);
                                        preferenceManager.putString(Constants.KEY_EMAIL, email);
                                        preferenceManager.putString(Constants.KEY_PHONE_NUMBER, phone);
                                        preferenceManager.putString(Constants.KEY_PASSWORD, password);
                                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                                        preferenceManager.putInteger(Constants.KEY_AVAILABILITY, 1);
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(exception -> {
                                        showToast("Unable to sign up!");
                                    });
                        } else {
                            showToast("Email is already taken!");
                        }
                    }
                });

    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.profileImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}