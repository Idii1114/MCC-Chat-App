package com.example.mccchatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mccchatapp.databinding.ItemContainterReceivedMessageBinding;
import com.example.mccchatapp.databinding.ItemContainterSentMessageBinding;
import com.example.mccchatapp.models.ChatMessage;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap) {
        receiverProfileImage = bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainterSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainterReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position), receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainterSentMessageBinding binding;

        SentMessageViewHolder(ItemContainterSentMessageBinding itemContainterSentMessageBinding) {
            super(itemContainterSentMessageBinding.getRoot());
            binding = itemContainterSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.getRoot().setOnClickListener(view -> {
                if (binding.textDateTime.getVisibility() == View.GONE) {
                    binding.textDateTime.setVisibility(View.VISIBLE);
                } else {
                    binding.textDateTime.setVisibility(View.GONE);
                }
            });
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {


        LinearLayout messageLayout;



        private final ItemContainterReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainterReceivedMessageBinding itemContainterReceivedMessageBinding) {
            super(itemContainterReceivedMessageBinding.getRoot());
            binding = itemContainterReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if (receiverProfileImage != null){
                binding.profileImage.setImageBitmap(receiverProfileImage);
            }
            binding.getRoot().setOnClickListener(view -> {
                if (binding.textDateTime.getVisibility() == View.GONE) {
                    binding.textDateTime.setVisibility(View.VISIBLE);
                } else {
                    binding.textDateTime.setVisibility(View.GONE);
                }
            });
        }

    }

}
