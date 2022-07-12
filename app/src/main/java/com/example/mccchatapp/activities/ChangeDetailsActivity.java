package com.example.mccchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.mccchatapp.R;
import com.example.mccchatapp.databinding.ActivityChangeDetailsBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ChangeDetailsActivity extends BaseActivity {

    private ActivityChangeDetailsBinding binding;
    private PreferenceManager preferenceManager;
    private DocumentReference documentReference;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private Uri imageUri;

    private String encodedImage;
    private String conversionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        loadUserDetails();

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListeners() {

        binding.back.setOnClickListener(view -> onBackPressed());
        binding.profileImage.setOnClickListener(view -> choosePicture());
        binding.buttonName.setOnClickListener(view -> changeName());
        binding.buttonEmail.setOnClickListener(view -> changeEmail());
        binding.buttonPhone.setOnClickListener(view -> changePhoneNumber());
        binding.buttonPassword.setOnClickListener(view -> changePassword());

    }

    private void changeName() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        Button button_close, button_update;
        EditText input_name;

        AlertDialog.Builder changeNameDialog = new AlertDialog.Builder(ChangeDetailsActivity.this);
        View view = LayoutInflater.from(ChangeDetailsActivity.this).inflate(R.layout.dialog_change_name, viewGroup, false);
        changeNameDialog.setCancelable(false);
        changeNameDialog.setView(view);

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        input_name = view.findViewById(R.id.input_name);
        button_update = view.findViewById(R.id.button_update);
        button_close = view.findViewById(R.id.button_close);

        final AlertDialog alertDialog = changeNameDialog.create();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));

                String updated_name = input_name.getText().toString();
                if (preferenceManager.getString(Constants.KEY_NAME).equals(updated_name)) {
                    showToast("Nothing has been changed!");
                } else {
                    documentReference.update(
                            Constants.KEY_NAME, updated_name
                    );
                    preferenceManager.putString(Constants.KEY_NAME, updated_name);
                    showToast("Update Successful!");
                    alertDialog.dismiss();
                    updateConversationName();
                }
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void changeEmail() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        Button button_close, button_update;
        EditText input_email, input_password;

        AlertDialog.Builder changeEmailDialog = new AlertDialog.Builder(ChangeDetailsActivity.this);
        View view = LayoutInflater.from(ChangeDetailsActivity.this).inflate(R.layout.dialog_change_email, viewGroup, false);
        changeEmailDialog.setCancelable(false);
        changeEmailDialog.setView(view);

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        input_email = view.findViewById(R.id.input_email);
        input_password = view.findViewById(R.id.input_password);
        button_update = view.findViewById(R.id.button_update);
        button_close = view.findViewById(R.id.button_close);

        final AlertDialog alertDialog = changeEmailDialog.create();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));

                String updated_email = input_email.getText().toString();
                String confirm_password = input_password.getText().toString();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(preferenceManager.getString(Constants.KEY_EMAIL),
                                confirm_password);

                if (preferenceManager.getString(Constants.KEY_EMAIL).equals(updated_email)
                        && preferenceManager.getString(Constants.KEY_PASSWORD).equals(confirm_password)) {
                    showToast("Nothing has been changed!");
                } else if (!preferenceManager.getString(Constants.KEY_PASSWORD).equals(confirm_password)) {
                    showToast("Confirm password is invalid!");
                } else {
                    documentReference.update(
                            Constants.KEY_EMAIL, updated_email
                    );
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updateEmail(updated_email);
                                    preferenceManager.putString(Constants.KEY_EMAIL, updated_email);
                                }
                            });
                    showToast("Update Successful!");
                    alertDialog.dismiss();
                }
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void changePhoneNumber() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        Button button_close, button_update;
        EditText input_phone;

        AlertDialog.Builder changeNameDialog = new AlertDialog.Builder(ChangeDetailsActivity.this);
        View view = LayoutInflater.from(ChangeDetailsActivity.this).inflate(R.layout.dialog_change_phone_number, viewGroup, false);
        changeNameDialog.setCancelable(false);
        changeNameDialog.setView(view);

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        input_phone = view.findViewById(R.id.input_phone);
        button_update = view.findViewById(R.id.button_update);
        button_close = view.findViewById(R.id.button_close);

        final AlertDialog alertDialog = changeNameDialog.create();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_PHONE_NUMBER, preferenceManager.getString(Constants.KEY_PHONE_NUMBER));

                String updated_phone = input_phone.getText().toString();

                if (updated_phone.length() != 11) {
                    showToast("Invalid Phone Number");
                } else if (preferenceManager.getString(Constants.KEY_PHONE_NUMBER).equals(updated_phone)) {
                    showToast("Nothing has been changed!");
                } else if (updated_phone.length() == 11){
                    String firstNum = updated_phone.substring(0, 1);
                    String secondNum = updated_phone.substring(1, 2);
                    if ( firstNum.matches("0") || secondNum.matches("9") || Pattern.matches("[0-9]+", updated_phone)) {
                        documentReference.update(
                                Constants.KEY_PHONE_NUMBER, updated_phone
                        );
                        preferenceManager.putString(Constants.KEY_PHONE_NUMBER, updated_phone);
                        showToast("Update Successful!");
                        alertDialog.dismiss();
                    }
                } else {
                    showToast("Invalid Phone Number");
                }
            }
        });

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void changePassword() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        Button button_close, button_update;
        EditText input_oldPassword, input_password, input_confirmPassword;

        AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(ChangeDetailsActivity.this);
        View view = LayoutInflater.from(ChangeDetailsActivity.this).inflate(R.layout.dialog_change_password, viewGroup, false);
        changePasswordDialog.setCancelable(false);
        changePasswordDialog.setView(view);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        input_oldPassword = view.findViewById(R.id.input_oldPassword);
        input_password = view.findViewById(R.id.input_password);
        input_confirmPassword = view.findViewById(R.id.input_confirmPassword);
        button_update = view.findViewById(R.id.button_update);
        button_close = view.findViewById(R.id.button_close);

        final AlertDialog alertDialog = changePasswordDialog.create();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_password = input_oldPassword.getText().toString();
                String password = input_password.getText().toString();
                String confirm_password = input_confirmPassword.getText().toString();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(preferenceManager.getString(Constants.KEY_EMAIL),
                                old_password);

                if (password.length() <6) {
                    showToast("Password must be at least 6 characters or above!");
                } else if (!password.equals(confirm_password)){
                    showToast("Password and Confirm Password are not the same!");
                } else if (old_password.equals(password) || old_password.equals(confirm_password)) {
                    showToast("Nothing has been changed!");
                } else {
                    preferenceManager.putString(Constants.KEY_PASSWORD, password);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updatePassword(password);
                                }
                            });
                    showToast("Update Successful!");
                    alertDialog.dismiss();
                }
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void loadUserDetails() {

        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.profileImage.setImageBitmap(bitmap);

    }

    private void choosePicture() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageUri = intent.getData();
        binding.profileImage.setImageURI(imageUri);
        pickImage.launch(intent);

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
                            updateImage();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

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

    private void updateImage() {

        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));

        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));

        updateConversationImage();
    }

    private void updateConversationImage() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                                        .document(document.getId())
                                        .update(
                                                Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE)
                                        );
                            }
                        }
                    }
                });
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                                        .document(document.getId())
                                        .update(
                                                Constants.KEY_RECEIVER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE)
                                        );
                            }
                        }
                    }
                });
    }

    private void updateConversationName() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                                        .document(document.getId())
                                        .update(
                                                Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME)
                                        );
                            }
                        }
                    }
                });
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                                        .document(document.getId())
                                        .update(
                                                Constants.KEY_RECEIVER_NAME, preferenceManager.getString(Constants.KEY_NAME)
                                        );
                            }
                        }
                    }
                });
    }


}
