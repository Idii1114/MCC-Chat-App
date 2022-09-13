package com.example.mccchatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mccchatapp.databinding.ItemContainerRecentConversationBinding;
import com.example.mccchatapp.databinding.ItemContainerUserListBinding;
import com.example.mccchatapp.listeners.ConversionListener;
import com.example.mccchatapp.listeners.UserListListener;
import com.example.mccchatapp.models.ChatMessage;
import com.example.mccchatapp.models.User;
import com.example.mccchatapp.models.UserList;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ConversationViewHolder> {

    private final List<UserList> userLists;
    private final UserListListener userListListener;

    public UserListAdapter(List<UserList> userLists, UserListListener userListListener) {
        this.userLists = userLists;
        this.userListListener = userListListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(
            ItemContainerUserListBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false

            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(userLists.get(position));
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserListBinding binding;

        ConversationViewHolder(ItemContainerUserListBinding itemContainerUserListBinding) {
            super(itemContainerUserListBinding.getRoot());
            binding = itemContainerUserListBinding;

        }

        void setData(UserList userList) {

            binding.profileImage.setImageBitmap(getConversationImage(userList.conversationImage));
            binding.textName.setText(userList.conversationName);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = userList.conversationId;
                user.name = userList.conversationName;
                user.image = userList.conversationImage;
                userListListener.onUserListClicked(user);
            });

        }

    }

    private Bitmap getConversationImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
