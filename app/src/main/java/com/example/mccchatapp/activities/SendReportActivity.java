package com.example.mccchatapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.example.mccchatapp.databinding.ActivitySendReportBinding;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendReportActivity extends BaseActivity {

    private ActivitySendReportBinding binding;

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.back.setOnClickListener(view -> onBackPressed());
        binding.buttonSendReport.setOnClickListener(view -> sendReport());

    }

    private void sendReport() {

        try {

            String senderEmail = "mccfeedback01@gmail.com";
            String receiverEmail = "mccdatabase01@gmail.com";
            String senderPassword = "Melhamconstructioncorporation";

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            mimeMessage.setSubject("MCC Chat App: Feedback Report");
            mimeMessage.setText(binding.inputFeedback.getText().toString());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        finish();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            showToast("Message sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
