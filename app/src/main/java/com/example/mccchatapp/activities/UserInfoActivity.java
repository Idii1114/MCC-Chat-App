package com.example.mccchatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mccchatapp.databinding.ActivityUserInfoBinding;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;


public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;
    private PreferenceManager preferenceManager;

    private String receiverImage;
    private String receiverName;
    private String receiverEmail;
    private String receiverPhone;

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        info();

    }

    private void setListeners() {
        binding.back.setOnClickListener(view -> onBackPressed());
        binding.buttonEmail.setOnClickListener(view -> email());
        binding.buttonCall.setOnClickListener(view -> call());
        binding.buttonSms.setOnClickListener(view -> sms());
    }

    public void info() {
        Bundle bundle = getIntent().getExtras();
        receiverImage = bundle.getString(Constants.KEY_RECEIVER_IMAGE);
        receiverName = bundle.getString(Constants.KEY_RECEIVER_NAME);
        receiverEmail = bundle.getString(Constants.KEY_RECEIVER_EMAIL);
        receiverPhone = bundle.getString(Constants.KEY_RECEIVER_PHONE);

        binding.profileImage.setImageBitmap(getBitmapFromEncodedString(receiverImage));
        binding.textName.setText(receiverName);
    }

    public void email() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, receiverEmail);
        intent.setType("plaint/text");
        startActivity(intent);
    }

    public void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + receiverPhone));
        startActivity(intent);
    }

    public void sms() {
        Uri uri = Uri.parse("smsto:" + receiverPhone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(intent);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
